package ui.session

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import config.Session
import config.Shell
import config.Ssh


@Composable
fun AddSessionModal(
    addSession: (session: Session) -> Unit,
    onCloseRequest: () -> Unit
) {
    var type by remember { mutableStateOf("SHELL") }
    Surface(modifier = Modifier.fillMaxSize()) {
        CenteredColumn(modifier = Modifier.fillMaxHeight()) {
            CenteredRow(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { type = "SHELL" })
                { Text("SHELL") }
                Button(onClick = { type = "SSH" })
                { Text("SSH") }
            }
            if ("SSH" == type) {
                AddSshSession(addSession, onCloseRequest)
            }
            if ("SHELL" == type) {
                AddShellSession(addSession, onCloseRequest)
            }
        }
    }
}

@Composable
fun CenteredColumn(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) =
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        children()
    }


@Composable
fun CenteredRow(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) =
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        children()
    }

@Composable
fun AddSshSession(onConfirmClick: (session: Session) -> Unit, onCloseRequest: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf(22) }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    CenteredColumn {
        Column(verticalArrangement = Arrangement.Center) {
            SessionEditTextField("Name", name) { name = it }
            SessionEditTextField("Group", group) { group = it }
            SessionEditTextField("Host", host) { host = it }
            SessionEditPortField("Port", port) { port = it }
            SessionEditTextField("User", user) { user = it }
            SessionEditPasswordField("Password", password) { password = it }
        }
        CenteredRow {
            Button(modifier = Modifier.padding(4.dp),
                onClick = {
                    val remoteSession = Session(
                        name, group, "SSH", null, null, Ssh(
                            host, port, "xterm-256color", user, password
                        )
                    )
                    onConfirmClick(remoteSession)
                    onCloseRequest()
                }) {
                Text("Confirm")
            }
            Button(
                modifier = Modifier.padding(4.dp),
                onClick = onCloseRequest
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun AddShellSession(onConfirmClick: (session: Session) -> Unit, onCloseRequest: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    Column {
        Column {
            SessionEditTextField("Name", name) { name = it }
            SessionEditTextField("Group", group) { group = it }
            SessionEditTextField("Command", command) { command = it }
        }
        Row {
            Button(modifier = Modifier.padding(4.dp),
                onClick = {
                    val localSession = Session(name, group, "SHELL", null, Shell(command), null)
                    onConfirmClick(localSession)
                    onCloseRequest()
                }) {
                Text("Confirm")
            }
            Button(
                modifier = Modifier.padding(4.dp),
                onClick = onCloseRequest
            ) {
                Text("Cancel")
            }
        }
    }


}