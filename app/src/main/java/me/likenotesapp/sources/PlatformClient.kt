package me.likenotesapp.sources


import me.likenotesapp.UpdatableState

class PlatformClient {
    val requests = mutableListOf<RequestToPlatform>()
    val currentRequest = UpdatableState<RequestToPlatform>(RequestToPlatform.Initial)

    suspend inline fun <reified T> request(request: RequestToPlatform.Save<String>, onResponse: (T) -> Unit) {
        val response = requestUpdatable<T>(request).await()
        onResponse(response)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> requestUpdatable(newRequest: RequestToPlatform): UpdatableState<T> {
        println("newRequest: $newRequest")

        requests.add(newRequest)
        currentRequest.post(newRequest)

        val response = (newRequest as IResponsable<*>).response
        return response as UpdatableState<T>
    }

}

sealed class RequestToPlatform() {
    data class Save<T>(
        val data: T,
        override val response: UpdatableState<Boolean> = UpdatableState(false)
    ) : RequestToPlatform(), IResponsable<Boolean>

    object Initial : RequestToPlatform()

}