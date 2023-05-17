package ui.session

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import config.Session
import ui.AppTheme

@Composable
fun SessionSelectionStatusView(selectedSession: Session?) = Surface {
    selectedSession?.let {
        when (selectedSession.type) {
            "SHELL" -> ShellStatusView(selectedSession)
            "SSH" -> SshStatusView(selectedSession)
        }
    }
}

@Composable
fun ShellStatusView(session: Session) {
    Column(
        Modifier.fillMaxWidth()
            .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape)
    ) {
        session.shell?.let {
            RowText("Name", session.name)
            RowText("Command", it.command)
        }

    }
}

@Composable
fun SshStatusView(session: Session) {
    Column(
        Modifier.fillMaxWidth()
            .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape)
    ) {
        session.ssh?.let {
            RowText("Name", session.name)
            RowText("Host", it.host)
        }

    }
}


@Composable
fun RowText(title: String, value: String) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start
) {
    Text(title, modifier = Modifier.width(50.dp))
    Text(value)
}
