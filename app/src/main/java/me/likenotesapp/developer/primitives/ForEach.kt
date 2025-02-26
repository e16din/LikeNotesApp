package me.likenotesapp.developer.primitives

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlin.reflect.KClass

// NOTE: работа с обозреваемыми объектами схожа с работой в цикле for, 
// основная разница в том что тело цикла разбросано по всему проекту 
// и может дополняться/изменяться
class ForEach<T : Any>(initial: T? = null) {

    val values = mutableListOf<T>()
    val actions = Map<List<(value: T) -> Unit>>()

    val it: T
        get() = values.lastOrNull() ?: throw NullPointerException("the values must not be empty")

    fun itOrNull(): T? {
        return values.lastOrNull()
    }

    init {
        initial?.let {
            values.add(initial)
        }
    }

    fun onEach(
        key: Any = Unit,
        resetOther: Boolean = false,
        onChanged: (value: T) -> Unit
    ) {
        if (resetOther) {
            actions.clear()
        }

        val currentActions = actions[key] ?: emptyList()
        actions.add(key, currentActions + onChanged)
    }

    fun next(newValue: T, ifNew: Boolean = false) {
        if (!ifNew || (ifNew && it != newValue)) {
            values.add(newValue)

            actions.keys.forEach { key ->
                actions[key]?.forEach { onChange ->
                    onChange(newValue)
                }
            }
        }
    }

    fun reset() {
        actions.clear()
        values.clear()
    }

    fun repostTo(foreach: ForEach<T>, key: Any = Unit) {
        onEach(key) { value ->
            foreach.next(value)
        }
    }

    fun pop(): T {
        return values.removeAt(values.lastIndex)
    }
}

@Composable
fun <T : R, R : Any> ForEach<T>.collectAsState(
    key: KClass<*> = Unit::class,
    resetOther: Boolean = false
): State<R?> {
    val current = itOrNull()
    return produceState<R?>(current, if (current == null) null else this.it) {
        onEach(key, resetOther = resetOther) {
            value = it
        }
    }
}


// NOTE: представление кода в редакторе могло бы быть разным для каждого отдельного пользователя,
// это было бы удобно и устранило задачу добиваться единого стиля, и чистоты кода

// так же могло бы настраиваться положение пакетов и файлов в них,
// иногда удобнее сортировать не по алфавиту а по частоте использования например или по
// любой другой логике, это могло бы настраиваться локально