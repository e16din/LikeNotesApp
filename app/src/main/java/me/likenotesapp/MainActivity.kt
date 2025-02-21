package me.likenotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
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

        GlobalScope.launchWithHandler(Dispatchers.Main) {
            appFunction()
        }
    }

    suspend fun appFunction() {
        println("start")
        User.requester.request<Unit>(RequestData.Attention("Hello World!"))
            .await()

        println("step1")
        User.requester.request<Unit>(RequestData.Attention("Are you ready?"))
            .await()

        val item = User.requester.request<StartScreenButtonViewModel>(
            RequestData.Choice(
                listOf(
                    StartScreenButtonViewModel("Yes"),
                    StartScreenButtonViewModel("No")
                )
            )
        ).await()
        User.requester.request<Unit>(RequestData.Attention(item.name))
            .await()
        val note = User.requester.request<String>(RequestData.TextInput("Enter a Note"))
            .await()

        User.requester.request<Unit>(RequestData.Attention("Your Note: \n$note"))
            .await()

        appFunction()

//        val pass = User.requestText("get/text/password")
//
//
//        val loginChoice = User.requestChoice("get/choice/login")
//        loginChoice.await()
//
//        User.requestAttention("post/screen/loading")
//        val loginResult = Backend.requestData("post/login", email + pass)
//        loginResult.await()
//
//        Platform.requestDataSave("post/token/$", loginResult.token)
//
//        User.requestAttention("post/login/success/$", loginResult)
//        delay(1000)
//        User.requestAttention(if (loginResult) "post/screen/main" else "post/screen/auth")
    }
}

@Composable
fun RootView() {
    println("Root")
    val request = User.requester.request.collectAsState()

    val data = request.value.data
    println("data: $data")
    when (data) {
        is RequestData.Choice<*> -> SelectItemScreen(data)
        is RequestData.Attention -> MessageScreen(data)
        is RequestData.TextInput -> InputTextScreen(data)
        is RequestData.Pending -> CircularProgressIndicator()

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LikeNotesAppTheme {
        RootView()
    }
}

@Composable
fun <T> SelectItemScreen(data: RequestData.Choice<T>) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.align(Alignment.Center)) {
            this.items(items = data.items) { viewModel ->
                when (viewModel) {
                    is StartScreenButtonViewModel -> Button(onClick = {
                        data.chosen.post(viewModel)
                    }) {
                        Text(viewModel.name)
                    }

                    else -> Text("Not implemented")
                }
            }
        }
    }
}

data class StartScreenButtonViewModel(val name: String)


@Composable
fun InputTextScreen(data: RequestData.TextInput) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        TextField(
            value = text,
            label = { Text(data.label) },
            onValueChange = {
                text = it
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Button(onClick = {
            data.input.post(text)
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Готово")
        }
        Spacer(Modifier.weight(1f))
    }

}

@Composable
fun MessageScreen(data: RequestData.Attention) {
    println("MessageScreen")
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            data.message, modifier = Modifier
                .align(Alignment.Center)
                .clickable {
                    data.next.post(Unit)
                })
    }
}

//data class Item<T>(val viewModel: T)
sealed class RequestData() {
    class Attention(
        val message: String,
        val next: UpdatableState<Unit> = UpdatableState(Unit)
    ) : RequestData()

    class TextInput(
        val label: String,
        val input: UpdatableState<String> = UpdatableState("")
    ) : RequestData()

    class Choice<T>(
        val items: List<T>,
        var chosen: UpdatableState<T?> = UpdatableState(null)
    ) : RequestData()

    object Pending : RequestData()
}

class Request(
    val data: RequestData
)

class Requester {
    val requests = mutableListOf<Request>()
    val request = UpdatableState<Request>(Request(RequestData.Pending))

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> request(data: RequestData): UpdatableState<T> {
        val newRequest = Request(data)
        requests.add(newRequest)
        println("newRequest: ${newRequest.data}")
        request.post(newRequest)
        return when (data) {
            is RequestData.Attention -> data.next
            is RequestData.Choice<*> -> data.chosen
            is RequestData.TextInput -> data.input
            else -> {
                UpdatableState(null)
                //throw IllegalArgumentException()
            }
        } as UpdatableState<T>
    }
}

object User {
    val requester = Requester()
}

object Backend
object Platform


