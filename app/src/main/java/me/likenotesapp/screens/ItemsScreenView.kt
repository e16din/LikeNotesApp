package me.likenotesapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.likenotesapp.MainChoice
import me.likenotesapp.Note
import me.likenotesapp.NotesChoice
import me.likenotesapp.requests.ToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ItemsScreenView(request: ToUser.GetChoice<T>) {
    Box(modifier = Modifier.fillMaxSize()) {

        when (request.items.first()) {
            is MainChoice -> {
                LazyColumn(modifier = Modifier.align(Alignment.Companion.Center)) {
                    stickyHeader {
                        Text(text = request.title)
                    }

                    items(items = request.items.toMutableStateList()) { item ->
                        Button(
                            modifier = Modifier.requiredWidth(200.dp),
                            onClick = {
                                request.response.post(item)
                            }) {
                            Text((item as MainChoice).text)
                        }
                    }
                }
            }

            is Note -> {
                LazyColumn(modifier = Modifier.align(Alignment.Companion.Center)) {
                    stickyHeader {
                        Spacer(Modifier.height(64.dp))//Todo: remove it
                        IconButton(onClick = {
                            request.response.post(NotesChoice.Back())
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
                        }
                        Text(text = request.title)
                    }

                    items(items = request.items) { item ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                request.response.post(NotesChoice.Remove(item as Note))
                                true
                            }
                        )

                        SwipeToDismissBox(
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                            state = dismissState,
                            backgroundContent = {

                                // Здесь можно добавить фон для удаления
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(bottom = 4.dp),
                                    colors = CardDefaults.cardColors()
                                        .copy(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        Icon(
                                            Icons.Default.Delete,
                                            "delete",
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                    }
                                }
                            }
                        ) {
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                                    .clickable {
                                        request.response.post(NotesChoice.Select(item as Note))
                                    }) {
                                Text(
                                    (item as Note).text, modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            else -> Text("Not implemented")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenPreview() {
    LikeNotesAppTheme {
        ItemsScreenView(
            ToUser.GetChoice<Note>(
                title = "Title",
                items = mockNotes()
            )
        )
    }
}

@Composable
private fun mockNotes(): List<Note> = listOf(
    Note(
        text = "Note Text",
        createdMs = System.currentTimeMillis(),
        updatedMs = System.currentTimeMillis()
    ),
    Note(
        text = "Note Text 2",
        createdMs = System.currentTimeMillis(),
        updatedMs = System.currentTimeMillis()
    )
)
