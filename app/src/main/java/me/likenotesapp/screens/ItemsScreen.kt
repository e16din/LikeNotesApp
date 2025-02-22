package me.likenotesapp.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.likenotesapp.MainIntent
import me.likenotesapp.Note
import me.likenotesapp.requests.ToUser

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SelectItemScreen(request: ToUser.GetChoice<T>) {
    Box(modifier = Modifier.Companion.fillMaxSize()) {
        LazyColumn(modifier = Modifier.Companion.align(Alignment.Companion.Center)) {
            stickyHeader {
                Text(text = request.title)
            }

            items(items = request.items) { item ->
                when (item) {
                    is MainIntent -> {
                        Button(
                            modifier = Modifier.requiredWidth(200.dp),
                            onClick = {
                                request.response.post(item)
                            }) {
                            Text(item.text)
                        }
                    }

                    is Note -> {
                        Card(
                            Modifier.Companion
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, bottom = 4.dp)
                                .clickable {
                                    request.response.post(item)
                                }) {
                            Text(
                                item.text, modifier = Modifier.Companion.padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                )
                            )
                        }
                    }

                    else -> Text("Not implemented")
                }
            }
        }
    }
}