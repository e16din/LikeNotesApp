package me.likenotesapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import me.likenotesapp.Back
import me.likenotesapp.IMenuItem
import me.likenotesapp.MainMenu
import me.likenotesapp.Note
import me.likenotesapp.NotesChoice
import me.likenotesapp.TrashMenu
import me.likenotesapp.developer.primitives.requests.user.ToUser
import me.likenotesapp.headHeight
import me.likenotesapp.ui.theme.LikeNotesAppTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsScreenView(request: ToUser.GetChoice) {
    val oneItem = request.items.firstOrNull()
    val listState = rememberLazyListState()

    val headHeightPx = with(LocalDensity.current) { headHeight.dp.roundToPx().toFloat() }
    var headerOffsetHeightPx by remember { mutableFloatStateOf(0f) }

    val headerScrollEnabled = request.items.size > 0
            && oneItem !is MainMenu

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (headerScrollEnabled) {
                val delta = available.y
                val newOffset = headerOffsetHeightPx + delta
                headerOffsetHeightPx = newOffset.coerceIn(-headHeightPx, 0f)
            }

            return Offset.Zero
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        if (oneItem != null) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    top =
                        headHeight.dp + with(LocalDensity.current) { headerOffsetHeightPx.toDp() }
                )
            ) {

                fun provideKey(it: Any) = when (oneItem) {
                    is Note -> (it as Note).id
                    else -> it
                }

                items(
                    items = request.items.toMutableStateList(),
                    key = { provideKey(it) }) { item ->
                    when (oneItem) {
                        is MainMenu -> ButtonItemView(request, item as IMenuItem)
                        is TrashMenu -> ButtonItemView(request, item as IMenuItem)
                        is Note -> NoteItemView(request, item as Note)
                        else -> Text("Not Implemented")
                    }
                }
            }
        }
        Box(
            Modifier
                .height(headHeight.dp)
                .offset {
                    IntOffset(x = 0, y = headerOffsetHeightPx.roundToInt())
                }
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
}

@Composable
fun ButtonItemView(request: ToUser.GetChoice, item: IMenuItem) = with(request) {
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
fun NoteItemView(request: ToUser.GetChoice, item: Note) {
    @Composable
    fun NoteView() {
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

    val paddingModifier = Modifier.padding(start = 24.dp, end = 24.dp)
    if (item.removed) {
        Box(paddingModifier) {
            NoteView()
        }

    } else {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = {
                request.response.next(NotesChoice.Remove(item))
                true
            })

        SwipeToDismissBox(
            modifier = paddingModifier,
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
            })
        {
            NoteView()
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
                items = mockNotes()
            )
        )
    }
}


private fun mockButtons() = MainMenu.entries
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
