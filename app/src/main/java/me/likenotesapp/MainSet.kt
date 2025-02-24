package me.likenotesapp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.likenotesapp.requests.Client
import me.likenotesapp.requests.IHasAnyResponse
import me.likenotesapp.requests.IHasTypedResponse
import me.likenotesapp.requests.IRequest


const val headHeight = 258
val actualScope = GlobalScope

object User : Client() {

    suspend fun requestPrevious() {
        actualScope.launchWithHandler(Dispatchers.Main) {
            requestUpdatable.pop()
            val previous = requestUpdatable.pop()
            request(previous, true)
        }
    }

    suspend fun request(request: IRequest, isBlocked: Boolean = true) {
        actualScope.launchWithHandler(Dispatchers.Main) {
            requestUpdatable.post(request)
            handleResponse(request, isBlocked)
        }
    }

    private suspend fun handleResponse(request: IRequest, isBlocked: Boolean) {
        suspend fun onResponse() {
            println("onResponse: ${response.value}")
            if (request is IHasAnyResponse) {
                request.onResponse?.let { func ->
                    func(response.value)
                }
            }
        }

        if (isBlocked) {
            response.await()
            onResponse()

        } else {
            val key = "request"
            response.listen(key) {
                response.free(key)
                actualScope.launchWithHandler {
                    onResponse()
                }
            }
        }
    }
}

object Platform : Client() {
    suspend fun <T> request(request: IRequest, isBlocked: Boolean = true) {
        actualScope.launchWithHandler(Dispatchers.Main) {
            requestUpdatable.post(request)

            handleResponse<T>(request, isBlocked)
        }
    }

    private suspend fun <T> handleResponse(request: IRequest, isBlocked: Boolean) {
        suspend fun onResponse() {
            println("onResponse: ${response.value}")
            if (request is IHasTypedResponse<*>) {
                (request as IHasTypedResponse<T>).onResponse?.invoke(response.value as T)
            }
        }

        if (isBlocked) {
            response.await()
            onResponse()

        } else {
            val key = "request"
            response.listen(key) {
                response.free(key)
                actualScope.launchWithHandler {
                    onResponse()
                }
            }
        }
    }
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
