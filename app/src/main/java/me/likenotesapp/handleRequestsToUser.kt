package me.likenotesapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.likenotesapp.screens.InputTextScreenView
import me.likenotesapp.screens.MessageScreenView
import me.likenotesapp.screens.PendingScreenView
import me.likenotesapp.screens.ItemsScreenView
import me.likenotesapp.requests.ToUser

@Composable
fun handleRequestsToUser() {
    println("Root")
    val requestState = User.requestUpdatable.collectAsState()

    val request = requestState.value
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
        }
    }
}