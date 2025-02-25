package me.likenotesapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import me.likenotesapp.developer.primitives.collectAsState
import me.likenotesapp.developer.primitives.debug
import me.likenotesapp.screens.InputTextScreenView
import me.likenotesapp.screens.MessageScreenView
import me.likenotesapp.screens.PendingScreenView
import me.likenotesapp.screens.ItemsScreenView
import me.likenotesapp.requests.user.ToUser
import me.likenotesapp.requests.user.User

@Composable
fun handleRequestsToUser() {
    val requestState = User.request.collectAsState()

    LaunchedEffect(Unit) {
        appFunction()
    }

    val request = requestState.value
    debug {
        println("handleRequestsToUser: ${request?.javaClass?.simpleName}")
    }
    Box(
        modifier = Modifier
            .systemBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        when (request) {
            is ToUser.PostMessage -> MessageScreenView(request)
            is ToUser.PostLoadingMessage -> PendingScreenView(request)
            is ToUser.GetTextInput -> InputTextScreenView(request)
            is ToUser.GetChoice -> ItemsScreenView(request)
            else -> {
                debug {
                    println("handleRequestsToUser: Not Implemented { $request }")
                }
            }
        }
    }
}