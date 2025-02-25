package me.likenotesapp

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.DelicateCoroutinesApi
import me.likenotesapp.ui.theme.LikeNotesAppTheme

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = navigationBarStyle()
        )

        setContent {
            LikeNotesAppTheme {
                handleRequestsToUser()
            }
        }
        handleRequestsToPlatform(application)
    }

    private fun navigationBarStyle(): SystemBarStyle {
        val lightTransparentStyle = SystemBarStyle.light(
            scrim = WHITE,
            darkScrim = WHITE
        )
        val darkTransparentStyle = SystemBarStyle.light(
            scrim = BLACK,
            darkScrim = BLACK
        )
        val navigationBarStyle = if (resources.configuration.isNightModeActive)
            darkTransparentStyle
        else
            lightTransparentStyle
        return navigationBarStyle
    }
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    LikeNotesAppTheme {
        handleRequestsToUser()
    }
}





