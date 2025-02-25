package me.likenotesapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import me.likenotesapp.Back
import me.likenotesapp.MainChoice
import me.likenotesapp.Note
import me.likenotesapp.NotesChoice
import me.likenotesapp.requests.user.ToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsScreenView(request: ToUser.GetChoice) {
    val oneItem = request.items.first()

    val listState = rememberLazyListState()
    val isVisible = !listState.canScrollBackward || listState.lastScrolledBackward

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        stickyHeader {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut()
            ) {
                HeadView(
                    title = request.title,
                    withBack = request.canBack,
                    onBackClick = {
                        request.response.next(Back())
                    }
                )
            }
        }

        fun provideKey(it: Any) = when (oneItem) {
            is MainChoice -> {
                it
            }

            is Note -> {
                (it as Note).id
            }

            else -> {
                it
            }
        }

        items(items = request.items.toMutableStateList(), key = { provideKey(it) }) { item ->
            when (oneItem) {
                is MainChoice -> ButtonItemView(request, item)
                is Note -> NoteItemView(request, item)
                else -> Text("Not implemented")
            }
        }
    }
}

@Composable
fun ButtonItemView(request: ToUser.GetChoice, item: Any?) = with(request) {
    item as MainChoice
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier
                .requiredWidth(200.dp)
                .align(Alignment.Center),
            onClick = {
                request.response.next(item)
            }
        ) {

            Text(item.text)
        }
    }
}

@Composable
fun NoteItemView(request: ToUser.GetChoice, item: Any?) {
    item as Note
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            request.response.next(NotesChoice.Remove(item))
            true
        })

    SwipeToDismissBox(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp),
        state = dismissState,
        backgroundContent = {
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
        }) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .clickable {
                    request.response.next(NotesChoice.Select(item))
                }) {
            Text(
                item.text, modifier = Modifier.padding(
                    horizontal = 16.dp, vertical = 12.dp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemsScreenPreview() {
    LikeNotesAppTheme {
        ItemsScreenView(
            ToUser.GetChoice(
                title = "Title",
                items = mockButtons()
            )
        )
    }
}


private fun mockButtons() = MainChoice.entries
private fun mockNotes() = listOf(
    Note(
        text = "Note Text",
        createdMs = System.currentTimeMillis(),
        updatedMs = System.currentTimeMillis()
    ), Note(
        text = "Note Text 2",
        createdMs = System.currentTimeMillis(),
        updatedMs = System.currentTimeMillis()
    )
)
