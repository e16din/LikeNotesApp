package me.likenotesapp.requests

interface IHasAnyResponse {
    var onResponse:  ( (Any) -> Unit)?
}

interface IHasTypedResponse<T> {
    var onResponse:  ( (T) -> Unit)?
}