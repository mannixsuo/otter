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

    var name = ""
    var host = ""
    var port = ""
    var user = ""

    selectedSession?.let { session ->
        session.name.let {
            name = it
        }
        session.host.let {
            host = it
        }
        session.port.let {
            port = it.toString()
        }
        session.user.let {
            user = it
        }
    }

    Column(
        Modifier.fillMaxWidth()
            .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Name",
                modifier = Modifier.width(50.dp)
                    .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape)
            )
            Text(text = name)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                "Host", modifier = Modifier.width(50.dp)
                    .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape)
            )
            Text(host)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                "Port", modifier = Modifier.width(50.dp)
                    .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape)
            )
            Text(port)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(Dp.Hairline, AppTheme.colors.backgroundLight), RectangleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("User", modifier = Modifier.width(50.dp))
            Text(user)
        }
    }
}