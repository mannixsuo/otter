package ui.session

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview
@Composable
fun SessionSelectionTabView(onAddClick: () -> Unit) = Surface {
    Row() {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Session Selection",
                color = LocalContentColor.current.copy(alpha = 0.60f),
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            TextButton(onClick = onAddClick, modifier = Modifier.padding(2.dp, 0.dp)) {
                Text("ADD")
            }
        }
    }
}
