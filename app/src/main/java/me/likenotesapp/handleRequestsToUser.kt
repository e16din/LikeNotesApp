package me.likenotesapp

import androidx.compose.runtime.Composable
import me.likenotesapp.screens.InputTextScreenView
import me.likenotesapp.screens.MessageScreenView
import me.likenotesapp.screens.PendingScreenView
import me.likenotesapp.screens.ItemsScreenView
import me.likenotesapp.requests.ToUser

@Composable
fun handleRequestsToUser() {
    println("Root")
    val requestState = User.currentRequest.collectAsState()

    val request = requestState.value
    println("data: $request")
    when (request) {
        is ToUser.PostMessage -> MessageScreenView(request)
        is ToUser.PostLoadingMessage -> PendingScreenView(request)
        is ToUser.GetTextInput -> InputTextScreenView(request)
        is ToUser.GetChoice<*> -> ItemsScreenView(request)
    }
}