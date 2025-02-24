package me.likenotesapp.requests

import kotlinx.coroutines.Dispatchers
import me.likenotesapp.UpdatableState
import me.likenotesapp.actualScope
import me.likenotesapp.launchWithHandler

open class Client {
    var requestUpdatable = UpdatableState<IRequest>()
    var response = UpdatableState<Any>()

    fun clearAll() {
        actualScope.launchWithHandler(Dispatchers.Main) {
            requestUpdatable = UpdatableState<IRequest>()
            response = UpdatableState<Any>()
        }
    }
}

