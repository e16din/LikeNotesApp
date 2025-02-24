package me.likenotesapp.requests

interface IHasAnyResponse {
    var onResponse:  (suspend (Any) -> Unit)?
}

interface IHasTypedResponse<T> {
    var onResponse:  (suspend (T) -> Unit)?
}