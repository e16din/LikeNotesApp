package me.likenotesapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.likenotesapp.requests.ToUser
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun InputTextScreenView(request: ToUser.GetTextInput) {
    var text by remember { mutableStateOf(request.initial ?: "") }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        TextField(
            value = text,
            label = { Text(request.label) },
            onValueChange = {
                text = it
            },
            modifier = Modifier.align(Alignment.Companion.CenterHorizontally)
        )
        Button(onClick = {
            request.response.post(text)
        }, modifier = Modifier.align(Alignment.Companion.CenterHorizontally)) {
            Text(request.actionName)
        }
        Spacer(Modifier.weight(1f))
    }

}