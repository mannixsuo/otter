package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SideMenuItem(text: String) = Column(modifier = Modifier.padding(2.dp)) {
    text.forEach {
        Text(text = it.toString())
    }
}