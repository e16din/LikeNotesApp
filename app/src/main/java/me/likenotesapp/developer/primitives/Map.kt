package me.likenotesapp.developer.primitives

class Map<T> {
    val values = mutableListOf<T>()
    val keys = mutableListOf<Any>()

    fun add(key: Any, value: T) {
        keys.add(key)
        values.add(keys.size - 1, value)
    }

    fun remove(key: Any) {
        val i = keys.indexOf(key)
        values.removeAt(i)
        keys.remove(key)
    }

    fun clear() {
        values.clear()
        keys.clear()
    }

    operator fun set(key: Any, value: T) {
        val i = keys.indexOf(key)
        values[i] = value
    }

    operator fun get(key: Any): T? {
        val i = keys.indexOf(key)
        return (if (i < 0) null else values[i])
    }
}