package me.likenotesapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likenotesapp.headHeight
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@Composable
inline fun HeadView(
    title: String,
    withBack: Boolean = true,
    noinline onBackClick: () -> Unit = {},
    crossinline content: @Composable (() -> Unit) = { DefaultHeaderContent(title) },
    crossinline actionContent: @Composable (() -> Unit) = { }
) {
    Surface {
        Column {
            if (withBack) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                }
            }
            Box(
                Modifier.Companion
                    .height(headHeight.dp)
                    .fillMaxWidth()
            ) {
                Box(modifier = Modifier.align(Alignment.Companion.Center)) {
                    content()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    actionContent()
                }
            }
        }
    }
}

@Composable
fun DefaultHeaderContent(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
    )
}

@Preview(showBackground = true)
@Composable
fun HeadPreview() {
    LikeNotesAppTheme {
        Column {
            HeadView("Title", actionContent = {
                ElevatedButton(
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Сохранить")
                }
            })
            Text("Content")
        }
    }
}