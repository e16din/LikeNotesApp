package me.likenotesapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.likenotesapp.requests.Client
import me.likenotesapp.requests.ToPlatform
import me.likenotesapp.requests.ToUser


const val headHeight = 258

object User : Client() {
    val request = UpdatableState<ToUser>()
    fun requestPrevious() {
        request.pop()
        val prevRequest = request.pop()
        request.post(prevRequest)
    }
}

object Platform : Client() {
    val request = UpdatableState<ToPlatform>()
}

object Backend : Client()

inline fun CoroutineScope.launchWithHandler(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline launch: suspend () -> Unit
): Job {
    return launch(defaultContext + dispatcher) {
        launch.invoke()
    }
}

@OptIn(DelicateCoroutinesApi::class)
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    throw throwable
}

var defaultContext = SupervisorJob() + exceptionHandler
val actualScope = CoroutineScope(defaultContext + Dispatchers.Main)