package me.likenotesapp.developer.primitives.requests.user

import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.developer.primitives.requests.IRequest

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
        val canBack: Boolean = true
    ) : ToUser()

    data class GetChoice(
        val title: String,
        val items: List<Any>,
        val canBack: Boolean = true
    ) : ToUser()
}