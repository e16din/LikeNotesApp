package me.likenotesapp.developer.primitives.requests

import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.developer.primitives.requests.platform.Platform
import me.likenotesapp.developer.primitives.requests.platform.ToPlatform
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.developer.primitives.requests.user.User

fun <T : Any> IRequest<T>.onResponse(content: (T) -> Unit) {
    fun Any.short() = toString().replace("\n", "").take(64)

    response.onEach(resetOther = true) {
        debug {
            println("${this@onResponse.javaClass.simpleName}.response.value: ${it.short()}")
        }
        content(it)
    }

    when (this) {
        is ToUser -> {
            Platform.request.values.clear()
            User.request.next(this)
        }

        is ToPlatform -> Platform.request.next(this)
    }

    debug {
        println("===")
        println("===")
        println("===")
        println("New Request: ${this@onResponse.javaClass.simpleName}")

        User.request.values.forEach {
            println("User.request.value: ${it.short()}")
        }
        Platform.request.values.forEach {
            println("Platform.request.value: ${it.short()}")
        }
    }
}