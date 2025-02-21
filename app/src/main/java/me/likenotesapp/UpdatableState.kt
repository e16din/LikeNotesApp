package me.likenotesapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun CoroutineScope.launchWithHandler(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline launch: suspend () -> Unit
): Job {
    return launch(defaultContext + dispatcher) {
        launch.invoke()
    }
}

@OptIn(DelicateCoroutinesApi::class)
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//    throwable.log()
//    if (throwable is UnauthorizedException) {
//        appModel?.onLogoutException()
//    } else {
        throw throwable
//    }
}

var defaultContext = SupervisorJob() + exceptionHandler

operator fun <T> UpdatableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

class UpdatableState<T>(initial: T) {

    private var _value: T = initial
    var value: T
        get() = _value
        set(value) {
            throw IllegalStateException("Use '.post($value)' instead")
        }

    private var callbacks = mutableMapOf<Any, List<(value: T) -> Unit>>()

    fun listen(key: Any = Unit, onChanged: (value: T) -> Unit) {
        callbacks[key] = callbacks[key]?.let {
            it + onChanged
        } ?: listOf(onChanged)
    }

    suspend fun await() = suspendCoroutine { continuation ->
        listen {
            continuation.resumeWith(
                Result.success(it)
            )
        }
    }


    fun post(value: T, scope: CoroutineScope = GlobalScope, ifNew: Boolean = false) {
        if (!ifNew || (ifNew && _value != value)) {
            scope.launchWithHandler(Dispatchers.Main) {
                _value = value
                callbacks.keys.forEach {
                    callbacks[it]?.forEach { onChange ->
                        onChange(value)
                    }
                }
            }
        }
    }

    fun free(key: Any) {
        callbacks.remove(key)
    }

    fun repostTo(state: UpdatableState<T>, key: Any = Unit) {
        listen(key) { value ->
            state.post(value)
        }
    }
}

@Composable
fun <T : R, R> UpdatableState<T>.collectAsState(
    key: KClass<*> = Unit::class,
    initial: R = this.value,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = produceState(initial, this.value) {
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