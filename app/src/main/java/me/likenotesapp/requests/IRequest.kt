package me.likenotesapp.requests

import me.likenotesapp.developer.primitives.ForEach

interface IRequest<T: Any> {
    val response: ForEach<T>
}