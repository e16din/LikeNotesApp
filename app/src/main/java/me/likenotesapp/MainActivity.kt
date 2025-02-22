package me.likenotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        User
        Platform
        Backend

        enableEdgeToEdge()
        setContent {
            LikeNotesAppTheme {
                handleRequestsToUser()
            }
        }

        GlobalScope.launchWithHandler(Dispatchers.Main) {
            handleRequestsToPlatform(application)
            appFunction()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    LikeNotesAppTheme {
        handleRequestsToUser()
    }
}





