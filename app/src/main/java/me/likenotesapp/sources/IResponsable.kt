package me.likenotesapp.sources

import me.likenotesapp.UpdatableState

interface IResponsable<T> {
    val response: UpdatableState<T>
}