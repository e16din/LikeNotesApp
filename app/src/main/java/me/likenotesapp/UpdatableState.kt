package me.likenotesapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass


class UpdatableState<T : Any?>(initial: T? = null, val scope: CoroutineScope = actualScope) {
    var values = mutableListOf<T?>()

    init {
        initial?.let {
            values.add(initial)
        }
    }

    var value: T
        get() = values.lastOrNull() ?: throw NullPointerException("Check this value")
        set(value) {
            throw IllegalStateException("Use '.post($value)' instead")
        }

    private var callbacks = mutableMapOf<Any, List<(value: T?) -> Unit>>()

    fun listen(key: Any = Unit, onChanged: (value: T?) -> Unit) {
        callbacks[key] = callbacks[key]?.let { callbacks ->
            callbacks + onChanged
        } ?: listOf(onChanged)
    }

    suspend fun await(): T? {
        return suspendCoroutine { continuation ->
            val key = "await"
            listen(key) {
                free(key)
                continuation.resumeWith(
                    Result.success(it)
                )
            }
        }
    }

    fun post(newValue: T?, ifNew: Boolean = false) {
        if (!ifNew || (ifNew && value != newValue)) {
            values.add(newValue)
            scope.launchWithHandler(Dispatchers.Main) {
                callbacks.keys.forEach { key ->
                    callbacks[key]?.forEach { onChange ->
                        onChange(newValue)
                    }
                }
                values.forEach {
                    println("list: $it")
                }
            }
        }
    }

    fun free(key: Any) {
        scope.launchWithHandler(Dispatchers.Main) {
            callbacks.remove(key)
        }
    }

    fun repostTo(state: UpdatableState<T>, key: Any = Unit) {
        listen(key) { value ->
            state.post(value)
        }
    }

    fun pop(): T {
        return values.removeAt(values.lastIndex) ?: throw NullPointerException()
    }
}

@Composable
fun <T : R, R : Any?> UpdatableState<T>.collectAsState(
    key: KClass<*> = Unit::class,
    initial: R? = null,
    context: CoroutineContext = EmptyCoroutineContext
): State<R?> = produceState(initial, if (initial == null) null else this.value) {
    if (context == EmptyCoroutineContext) {
        listen(key) { value = it }

    } else withContext(context) {
        listen(key) { value = it }
    }
}

@Composable
fun <V, T : List<V>> UpdatableState<T>.collectAsMutableStateList(
    key: Any,
    context: CoroutineContext = EmptyCoroutineContext
): State<SnapshotStateList<V>> {

    return produceState(
        (this.value as List<V>).toMutableStateList(),
        (this.value as List<V>).toMutableStateList(),
        context
    ) {
        if (context == EmptyCoroutineContext) {
            listen(key) { value = (it as List<V>).toMutableStateList() }
        } else withContext(context) {
            listen(key) { value = (it as List<V>).toMutableStateList() }
        }
    }
}

fun listenUpdates(
    vararg states: UpdatableState<Any>,
    onUpdate: () -> Unit
): UpdatableState<Any> {
    val updatable = UpdatableState<Any>(Unit)
    states.forEach {
        it.listen {
            onUpdate()
            updatable.post(Unit)
        }
    }

    return updatable
}