package me.likenotesapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likenotesapp.Back
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.maxNoteLength
import me.likenotesapp.maxNoteLines

import me.likenotesapp.ui.theme.LikeNotesAppTheme

@Composable
fun InputTextScreenView(request: ToUser.GetTextInput) {
    var text by remember { mutableStateOf(request.initial ?: "") }
    Column {
        HeadView(
            request.title,
            onBackClick = {
                request.response.next(Back())
            },
            withBack = request.canBack,
            actionContent = {
                ElevatedButton(onClick = {
                    request.response.next(text)
                }, modifier = Modifier) {
                    Text(request.actionName)
                }
            }
        )

        TextField(
            value = text,
            label = { Text(request.label) },
            maxLines = maxNoteLines,
            onValueChange = {
                if (it.length <= maxNoteLength && it.count({ it == '\n' }) < maxNoteLines) {
                    text = it
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                .align(Alignment.Companion.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InputTextScreenPreview() {
    LikeNotesAppTheme {
        InputTextScreenView(
            ToUser.GetTextInput(
                title = "Title", label = "Label"
            )
        )
    }
}