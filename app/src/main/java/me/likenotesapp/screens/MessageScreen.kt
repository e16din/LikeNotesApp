package me.likenotesapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.likenotesapp.requests.ToUser

@Composable
fun MessageScreen(request: ToUser.PostMessage) {
    println("MessageScreen")
    Column(modifier = Modifier.Companion.fillMaxSize()) {
        Spacer(Modifier.Companion.weight(1f))
        Text(
            request.message,
            modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)
        )
        Button(onClick = {
            request.response.post(Unit)
        }, modifier = Modifier.Companion.align(Alignment.Companion.CenterHorizontally)) {
            Text(request.actionName)
        }
        Spacer(Modifier.Companion.weight(1f))
    }
}