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
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@Composable
fun PendingScreenView(request: ToUser.PostLoadingMessage) {
    HeadView(
        title = request.message,
        withBack = false,
        onBackClick = {
            request.response.next(Cancel())
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