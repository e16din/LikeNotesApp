package me.likenotesapp.developer.primitives.requests.user

import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.developer.primitives.requests.IRequest

sealed class ToUser() : IRequest<Any> {
    override val response: ForEach<Any> = ForEach<Any>()

    data class PostMessage(
        val message: String,
        val actionName: String = "Дальше",
        val type: Type = Type.Default
    ) : ToUser() {
        enum class Type {
            Default,
            Loading
        }
    }

    data class GetString(
        val title: String,
        val label: String,
        var data: String? = null,
        val actionName: String = "Дальше",
        val canBack: Boolean = true,
        val type: Type = Type.Short
    ) : ToUser() {
        enum class Type {
            Short,
            Long
        }
    }

    data class GetChoice(
        val title: String,
        val items: List<Any>,
        val canBack: Boolean = true,
        val type: Type = Type.Default
    ) : ToUser()  {
        enum class Type {
            Default
        }
    }
}