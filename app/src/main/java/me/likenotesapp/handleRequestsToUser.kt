package me.likenotesapp

import androidx.compose.runtime.Composable
import me.likenotesapp.screens.InputTextScreen
import me.likenotesapp.screens.MessageScreen
import me.likenotesapp.screens.PendingScreen
import me.likenotesapp.screens.SelectItemScreen
import me.likenotesapp.requests.ToUser

@Composable
fun handleRequestsToUser() {
    println("Root")
    val requestState = User.currentRequest.collectAsState()

    val request = requestState.value
    println("data: $request")
    when (request) {
        is ToUser.PostMessage -> MessageScreen(request)
        is ToUser.PostLoadingMessage -> PendingScreen(request)
        is ToUser.GetTextInput -> InputTextScreen(request)
        is ToUser.GetChoice<*> -> SelectItemScreen(request)
    }
}