package me.likenotesapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likenotesapp.Cancel
import me.likenotesapp.User
import me.likenotesapp.requests.ToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@Composable
fun PendingScreenView(request: ToUser.PostLoadingMessage) {
    println("PendingScreen")
    HeadView(
        title = request.message,
        withBack = false,
        onClick = {
            User.response.post(Cancel())
        },
        content = {
            Column {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.CenterHorizontally)
                )
                DefaultHeaderContent(request.message)
            }
        },
        actionContent = {
            ElevatedButton(
                onClick = {},
                modifier = Modifier) {
                Text("Отмена")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PendingMessageScreenPreview() {
    LikeNotesAppTheme {
        PendingScreenView(
            ToUser.PostLoadingMessage(
                message = "Message"
            )
        )
    }
}