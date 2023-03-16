package ui.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import config.Session
import platform.grayBorder

@Composable
fun UpdateSession(session: Session) {
    var name by mutableStateOf(session.name)
    var host by mutableStateOf(session.host)
    var port by mutableStateOf(session.port)
    var user by mutableStateOf(session.user)
    var password by mutableStateOf(session.password)

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                TextButton(modifier = Modifier.padding(2.dp), onClick = {}) {
                    Text("Confirm")
                }
                TextButton(modifier = Modifier.padding(2.dp), onClick = {}) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun SessionEditTextField(title: String, value: String, onValueChange: (String) -> Unit) =
    OutlinedTextField(modifier = Modifier.padding(PaddingValues(0.dp, 4.dp)),
        singleLine = true,
        label = { Text(title) },
        value = value,
        onValueChange = { onValueChange(it) })

@Composable
fun SessionEditPortField(title: String, value: Int?, onValueChange: (Int) -> Unit) =

    OutlinedTextField(modifier = Modifier.padding(PaddingValues(0.dp, 4.dp)),
        singleLine = true,
        label = { Text(title) },
        value = value?.toString() ?: "",
        onValueChange = {
            if (it.matches(Regex("[0-9]+"))) {
                var port = it.toInt()
                if (port > 65535) {
                    port = 65535
                }
                if (port < 0) {
                    port = 0
                }
                onValueChange(port)
            }
        }
    )

@Composable
fun SessionEditPasswordField(title: String, value: String, onValueChange: (String) -> Unit) = OutlinedTextField(
    modifier = Modifier.padding(PaddingValues(0.dp, 4.dp)),
    singleLine = true,
    label = { Text(title) },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    visualTransformation = PasswordVisualTransformation(),
    value = value,
    onValueChange = onValueChange
)