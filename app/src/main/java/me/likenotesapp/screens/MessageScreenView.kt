package me.likenotesapp.screens

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.likenotesapp.User
import me.likenotesapp.requests.ToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@Composable
fun MessageScreenView(request: ToUser.PostMessage) {
    println("MessageScreen")
    HeadView(
        title = "",
        withBack = false,
        content = {
            Text(
                text = request.message,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        actionContent = {
            Button(onClick = {
                User.response.post(Unit)
            }, modifier = Modifier) {
                Text(request.actionName)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MessageScreenPreview() {
    LikeNotesAppTheme {
        MessageScreenView(
            ToUser.PostMessage(
                message = "Message"
            )
        )
    }
}