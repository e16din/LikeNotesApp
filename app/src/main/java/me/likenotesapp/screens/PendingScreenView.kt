package me.likenotesapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.likenotesapp.requests.ToUser

@Composable
fun PendingScreenView(request: ToUser.PostLoadingMessage) {
    println("PendingScreen")
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Text(
            text = request.message,
            modifier = Modifier.align(Alignment.Companion.CenterHorizontally)
        )
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Companion.CenterHorizontally))
        Spacer(Modifier.weight(1f))
    }
}