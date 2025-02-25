package me.likenotesapp.requests.user

import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.requests.IClient

object User : IClient {
    val request = ForEach<ToUser>()

    fun requestPrevious() {
        request.pop()
        val prevRequest = request.pop()
        request.next(prevRequest)
    }
}