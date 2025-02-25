package me.likenotesapp.requests.user

import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.requests.IRequest

sealed class ToUser() : IRequest<Any> {
    override val response: ForEach<Any> = ForEach<Any>()

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