package me.likenotesapp.developer.primitives

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Worker<T : Any>(initial: T? = null) {
    var output = ForEach<T>(initial)
    var working = ForEach(false)
    var fail = ForEach(false)

    var isDoOnceCalled = false

    fun resetWith(newData: T) {
        output.next(newData)
        working.next(false, ifNew = true)
        fail.next(false, ifNew = true)
    }

    inline fun worker(doOnce: () -> Unit = {}): Worker<T> {
        if (!isDoOnceCalled) {
            doOnce()
            isDoOnceCalled = true
        }
        return this
    }

    fun data() = output.it
    fun hasFail() = fail.it

    inline fun act(crossinline task: suspend () -> Pair<T, Boolean>) {
        work {
            withContext(Dispatchers.Main) {
                working.next(true)
            }

            val result = task()

            withContext(Dispatchers.Main) {
                output.next(result.first)

                if (result.second) {
                    fail.next(false, ifNew = true)

                } else {
                    fail.next(true)
                }

                working.next(false)

                return@withContext result
            }
        }
    }
}

inline fun <T : Any> work(crossinline onDone: (T) -> Unit = {}, crossinline task: suspend () -> T) {
    actualScope.launchWithHandler {
        val result = task()

        debug {
            println("work result: $result")
        }

        withContext(Dispatchers.Main) {
            onDone(result)
        }
    }
}