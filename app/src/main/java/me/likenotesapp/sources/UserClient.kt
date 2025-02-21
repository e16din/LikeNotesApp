package me.likenotesapp.sources

import me.likenotesapp.UpdatableState

class UserClient {
    val requests = mutableListOf<RequestToUser>()
    val currentRequest = UpdatableState<RequestToUser>(RequestToUser.Attention(""))

    suspend inline fun <reified T> request(request: RequestToUser, onResponse: (T) -> Unit = {}) {
        val response = requestUpdatable<T>(request).await()
        onResponse(response)
    }
    inline fun <reified T> requestNotBlocked(request: RequestToUser) {
        requestUpdatable<T>(request)
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> requestUpdatable(newRequest: RequestToUser): UpdatableState<T> {
        println("newRequest: $newRequest")

        requests.add(newRequest)
        currentRequest.post(newRequest)

        val response = (newRequest as IResponsable<*>).response
        return response as UpdatableState<T>
    }
}

sealed class RequestToUser() {
    data class Attention(
        val message: String,
        val actionName: String = "Дальше",
        val isLoading:Boolean = false,
        override val response: UpdatableState<Unit> = UpdatableState(Unit)
    ) : RequestToUser(), IResponsable<Unit>

    data class TextInput(
        val label: String,
        val actionName: String = "Дальше",
        override val response: UpdatableState<String> = UpdatableState("")
    ) : RequestToUser(), IResponsable<String>

    data class Choice<T>(
        val title: String,
        val items: List<T>,
        override var response: UpdatableState<T?> = UpdatableState(null)
    ) : RequestToUser(), IResponsable<T?>
}

data class ButtonItem(val name: String)