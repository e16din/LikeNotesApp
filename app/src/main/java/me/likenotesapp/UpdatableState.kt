package me.likenotesapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass


class UpdatableState<T : Any?>(initial: T? = null) {
    private var values = mutableListOf<T?>()

    init {
        initial?.let {
            values.add(initial)
        }
    }

    var value: T
        get() = values.lastOrNull() ?: throw NullPointerException("Check this value")
        set(value) {
            throw IllegalStateException("Use '.post($value)' instead")
        }

    private var listeners = mutableMapOf<Any, List<(value: T?) -> Unit>>()

    fun listen(key: Any = Unit, onChanged: (value: T?) -> Unit) {
        listeners[key] = listeners[key]?.let { callbacks ->
            callbacks + onChanged
        } ?: listOf(onChanged)
    }

    suspend fun await(): T? {
        return suspendCoroutine { continuation ->
            val key = "await"
            listen(key) {
                freeListener(key)
                continuation.resumeWith(
                    Result.success(it)
                )
            }
        }
    }

    fun post(newValue: T?, ifNew: Boolean = false) {
        actualScope.launchWithHandler(Dispatchers.Main) {
            if (!ifNew || (ifNew && value != newValue)) {

                values.add(newValue)

                listeners.keys.forEach { key ->
                    listeners[key]?.forEach { onChange ->
                        onChange(newValue)
                    }
                }

                values.forEach {
                    println("list: $it")
                }
            }
        }
    }

    fun freeListener(key: Any) {
        listeners.remove(key)
    }

    fun freeAllListeners() {
        listeners.clear()
    }

    fun repostTo(state: UpdatableState<T>, key: Any = Unit) {
        listen(key) { value ->
            state.post(value)
        }
    }

    fun pop(): T {
        return values.removeAt(values.lastIndex) ?: throw NullPointerException()
    }
}

@Composable
fun <T : R, R : Any?> UpdatableState<T>.collectAsState(
    key: KClass<*> = Unit::class,
    initial: R? = null,
): State<R?> = produceState(initial, if (initial == null) null else this.value) {
    listen(key) {
        value = it
    }
}

fun listenUpdates(
    vararg states: UpdatableState<Any>,
    onUpdate: () -> Unit
): UpdatableState<Any> {
    val updatable = UpdatableState<Any>(Unit)
    states.forEach {
        it.listen {
            onUpdate()
            updatable.post(Unit)
        }
    }

    return updatable
}