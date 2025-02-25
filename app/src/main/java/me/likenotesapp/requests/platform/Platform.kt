package me.likenotesapp.requests.platform

import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.requests.IClient

object Platform : IClient {
    val request = ForEach<ToPlatform>()
}