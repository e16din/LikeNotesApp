package me.likenotesapp.requests

import me.likenotesapp.UpdatableState

interface IRequest<T> {
    val response: UpdatableState<T>
}