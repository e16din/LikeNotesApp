package me.likenotesapp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.likenotesapp.developer.primitives.collectAsState
import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.developer.primitives.requests.user.User
import me.likenotesapp.screens.InputTextScreenView
import me.likenotesapp.screens.ItemsScreenView
import me.likenotesapp.screens.MessageScreenView
import me.likenotesapp.screens.PendingScreenView

@Composable
fun handleRequestsToUser() {
    val requestState = User.request.collectAsState(resetOther = true)

    val request = requestState.value
    debug {
        println("handleRequestsToUser: ${request?.javaClass?.simpleName}")
    }

    BackHandler {
        request?.response?.next(Back())
    }

    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        when (request) {
            is ToUser.PostMessage -> {
                when (request.type) {
                    ToUser.PostMessage.Type.Default -> MessageScreenView(request)
                    ToUser.PostMessage.Type.Loading -> PendingScreenView(request)
                }
            }

            is ToUser.GetString -> InputTextScreenView(request)
            is ToUser.GetChoice -> ItemsScreenView(request)
            else -> {
                debug {
                    println("handleRequestsToUser: Not Implemented { $request }")
                }
            }
        }
    }
}