package me.likenotesapp.developer.primitives.requests.platform

import me.likenotesapp.developer.primitives.ForEach
import me.likenotesapp.developer.primitives.requests.IClient

object Platform : IClient {
    val request = ForEach<ToPlatform>()
}