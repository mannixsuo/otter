package ui.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SessionIcon(modifier: Modifier) = Box(modifier.size(24.dp).padding(4.dp)) {
    Icon(
        painterResource(
            "icon/terminal_black_24dp.svg",
        ), contentDescription = null, tint = Color(0xFF87939A)
    )
}