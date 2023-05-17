package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SideMenu(children: @Composable () -> Unit) =
    Column(modifier = Modifier
        .width(24.dp)
        .fillMaxHeight()
        .border(1.dp, MaterialTheme.colors.surface)) { children() }