package me.likenotesapp.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun screenHeightPx(): Float {
    val configuration = LocalConfiguration.current
    return with(LocalDensity.current) { configuration.screenHeightDp.dp.roundToPx().toFloat() }
}