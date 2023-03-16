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


@Composable
fun AddSessionModal(onConfirmClick: (session: Session) -> Unit, onCloseRequest: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("") }
    var port by remember { mutableStateOf(22) }
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(verticalArrangement = Arrangement.Center) {
                SessionEditTextField("Name", name) { name = it }
                SessionEditTextField("Host", host) { host = it }
                SessionEditPortField("Port", port) { port = it }
                SessionEditTextField("User", user) { user = it }
                SessionEditPasswordField("Password", password) { password = it }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(modifier = Modifier.padding(4.dp),
                    onClick = {
                        val session = Session(name, host, port, user, password)
                        onConfirmClick(session)
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
}