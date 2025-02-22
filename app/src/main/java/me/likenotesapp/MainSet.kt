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


object User : Client()
object Platform : Client()
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
