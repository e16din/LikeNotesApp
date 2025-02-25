package me.likenotesapp.developer.primitives

inline fun debug(action: () -> Unit) {
    if (develop) {
        action.invoke()
    }
}