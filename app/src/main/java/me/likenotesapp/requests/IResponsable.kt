package me.likenotesapp.requests

import me.likenotesapp.UpdatableState

interface IResponsable<T> {
    val response: UpdatableState<T>
}