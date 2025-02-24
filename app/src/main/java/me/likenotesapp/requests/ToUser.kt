package me.likenotesapp.requests

import me.likenotesapp.UpdatableState

sealed class ToUser() : IRequest<Any> {
    override val response: UpdatableState<Any> = UpdatableState<Any>()

    data class PostMessage(
        val message: String,
        val actionName: String = "Дальше",
        ) : ToUser()

    data class PostLoadingMessage(
        val message: String = "",

    ) : ToUser()

    data class GetTextInput(
        val title: String,
        val label: String,
        val initial: String? = null,
        val actionName: String = "Дальше",

    ) : ToUser()

    data class GetChoice(
        val title: String,
        val items: List<Any>,
    ) : ToUser()
}