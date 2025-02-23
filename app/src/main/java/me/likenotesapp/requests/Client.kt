package me.likenotesapp.requests

import me.likenotesapp.UpdatableState

open class Client {
    val requests = mutableListOf<IRequest>(InitialRequest)
    val currentRequest = UpdatableState<IRequest>(InitialRequest)

    suspend inline fun <reified T> request(request: IRequest) {
        requestUpdatable<T>(request).await()
        if (request is IResponsable<*>) {
            request.invokeOnResponse()
        }
    }

    fun requestNotBlocked(request: ToUser) {
        println("newRequest: $request")

        requests.add(request)
        currentRequest.post(request)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> requestUpdatable(request: IRequest): UpdatableState<T> {
        println("newRequest: $request")

        requests.add(request)
        currentRequest.post(request)

        val response = (request as IResponsable<*>).response
        return response as UpdatableState<T>
    }

    fun clear() {
        requests.clear()
        requests.add(InitialRequest)
        currentRequest.post(InitialRequest)
    }

    suspend fun requestPrevious() {
        var previous = requests.dropLast(1).last()
        println("previous: $previous")
        clear()

        requests.add(previous)
        currentRequest.post(previous)

        if (previous is IResponsable<*>) {
            previous.response.await()
            previous.invokeOnResponse()
        }
    }
}

