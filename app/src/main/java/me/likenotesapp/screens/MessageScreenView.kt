package me.likenotesapp.screens

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@Composable
fun MessageScreenView(request: ToUser.PostMessage) {
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
                request.response.next(Unit)
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