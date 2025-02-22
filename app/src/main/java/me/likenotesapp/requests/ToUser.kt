package me.likenotesapp.requests

import me.likenotesapp.UpdatableState

sealed class ToUser() : IRequest {
    data class PostMessage(
        val message: String,
        val actionName: String = "Дальше",
        override val response: UpdatableState<Unit> = UpdatableState(Unit)
    ) : ToUser(), IResponsable<Unit>

    data class PostLoadingMessage(val message: String = "") : ToUser()

    data class GetTextInput(
        val label: String,
        val initial: String? = null,
        val actionName: String = "Дальше",
        override val response: UpdatableState<String> = UpdatableState("")
    ) : ToUser(), IResponsable<String>

    data class GetChoice<T>(
        val title: String,
        val items: List<T>,
        override var response: UpdatableState<T?> = UpdatableState(null)
    ) : ToUser(), IResponsable<T?>
}