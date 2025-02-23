package me.likenotesapp.requests

import me.likenotesapp.UpdatableState

interface IResponsable<T> {
    val response: UpdatableState<T>
    var onResponse:  (suspend (T) -> Unit)?

    suspend fun invokeOnResponse() {
        onResponse?.invoke(response.value)
    }
}