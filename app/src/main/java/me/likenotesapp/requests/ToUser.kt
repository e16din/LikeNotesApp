package me.likenotesapp.requests

sealed class ToUser() : IRequest, IHasAnyResponse {

    data class PostMessage(
        val message: String,
        val actionName: String = "Дальше",
        override var onResponse: (suspend (Any) -> Unit)? = null
    ) : ToUser()

    data class PostLoadingMessage(
        val message: String = "",
        override var onResponse: (suspend (Any) -> Unit)? = null
    ) : ToUser()

    data class GetTextInput(
        val title: String,
        val label: String,
        val initial: String? = null,
        val actionName: String = "Дальше",
        override var onResponse: (suspend (Any) -> Unit)? = null
    ) : ToUser()

    data class GetChoice(
        val title: String,
        val items: List<Any>,
        override var onResponse: (suspend (Any) -> Unit)? = null
    ) : ToUser()
}