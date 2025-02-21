package me.likenotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import me.likenotesapp.sources.ButtonItem
import me.likenotesapp.sources.RequestToPlatform
import me.likenotesapp.sources.RequestToUser
import me.likenotesapp.ui.theme.LikeNotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LikeNotesAppTheme {
                RootView()
            }
        }

        RootStore()

        GlobalScope.launchWithHandler(Dispatchers.Main) {
            appFunction()
        }
    }

    private fun RootStore() {
        Platform.client.currentRequest.listen { request ->
            when (request) {
                RequestToPlatform.Initial -> {
                    // do nothing
                }

                is RequestToPlatform.Save<*> -> request.response.post(true) //todo: implement Save
            }
        }
    }

    suspend fun appFunction() {
        User.client.request<Unit>(RequestToUser.Attention("Привет мир!", "Привет!")) {
            val yesItem = "Да"
            val noItem = "Нет"
            User.client.request<ButtonItem>(
                RequestToUser.Choice(
                    "Ты готов?",
                    listOf(
                        ButtonItem(yesItem),
                        ButtonItem(noItem)
                    )
                )
            ) { item ->
                when (item.name) {
                    yesItem -> {
                        User.client.request<String>(
                            RequestToUser.TextInput(
                                "Enter a Note",
                                "Сохранить"
                            )
                        ) { note ->
                            User.client.requestNotBlocked<Unit>(
                                RequestToUser.Attention(
                                    "Идет загрузка...",
                                    isLoading = true
                                )
                            )
                            Platform.client.request<Boolean>(RequestToPlatform.Save(note)) { success ->
                                val message = if (success)
                                    "Сохранение прошло успешно!"
                                else
                                    "Возникла ошибка при сохранении :("
                                User.client.request<Unit>(
                                    RequestToUser.Attention(
                                        message,
                                        "Океюшки"
                                    )
                                ) {
                                    appFunction()
                                }
                            }
                        }
                    }

                    noItem -> {
                        User.client.request<Unit>(
                            RequestToUser.Attention(
                                "Закончим на этом :)",
                                "Ок"
                            )
                        ) {
                            appFunction()
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun RootView() {
    println("Root")
    val request = User.client.currentRequest.collectAsState()

    val data = request.value
    println("data: $data")
    when (data) {
        is RequestToUser.Attention -> {
            if (data.isLoading) {
                PendingScreen(data)
            } else {
                MessageScreen(data)
            }
        }

        is RequestToUser.TextInput -> InputTextScreen(data)
        is RequestToUser.Choice<*> -> SelectItemScreen(data)
    }
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    LikeNotesAppTheme {
        RootView()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> SelectItemScreen(request: RequestToUser.Choice<T>) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.align(Alignment.Center)) {
            stickyHeader {
                Text(request.title)
            }

            items(items = request.items) { viewModel ->
                when (viewModel) {
                    is ButtonItem -> Button(onClick = {
                        request.response.post(viewModel)
                    }) {
                        Text(viewModel.name)
                    }

                    else -> Text("Not implemented")
                }
            }
        }
    }
}

@Composable
fun InputTextScreen(request: RequestToUser.TextInput) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        TextField(
            value = text,
            label = { Text(request.label) },
            onValueChange = {
                text = it
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Button(onClick = {
            request.response.post(text)
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(request.actionName)
        }
        Spacer(Modifier.weight(1f))
    }

}

@Composable
fun MessageScreen(request: RequestToUser.Attention) {
    println("MessageScreen")
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Text(
            request.message, modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Button(onClick = {
            request.response.post(Unit)
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(request.actionName)
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun PendingScreen(request: RequestToUser.Attention) {
    println("PendingScreen")
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}





