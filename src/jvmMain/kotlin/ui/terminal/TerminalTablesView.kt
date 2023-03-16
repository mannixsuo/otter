package ui.terminal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.AppTheme

@Composable
fun TerminalTablesView(model: Terminals) = Row(Modifier.horizontalScroll(rememberScrollState())) {
    for (terminal in model.terminals) {
        TerminalTableView(
            terminal.title,
            terminal.isActive,
            fun() { terminal.activate() },
            terminal.close
        )
    }

}

@Composable
fun TerminalTableView(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: (() -> Unit)?
) = Surface(
    shape = RectangleShape,
    elevation = if (isActive) {
        10.dp
    } else {
        0.dp
    },
    color = if (isActive) AppTheme.colors.backgroundDark else Color.Transparent
) {
    Row(
        Modifier
            .clickable(remember(::MutableInteractionSource), indication = null) {
                onClick.invoke()
            }
            .padding(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = LocalContentColor.current,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        if (onClose != null) {
            Icon(
                Icons.Default.Close,
                tint = LocalContentColor.current,
                contentDescription = "Close",
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                    .clickable {
                        onClose.invoke()
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(24.dp, 24.dp)
                    .padding(4.dp)
            )
        }
    }
}