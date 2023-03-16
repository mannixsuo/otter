package shell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import com.jcraft.jsch.UserInfo

@Composable
fun UserInfoCompose(show: Boolean, onCloseDialog: () -> Unit) {
    var pwd by remember { mutableStateOf("") }
    Dialog(
        visible = show,
        onCloseRequest = onCloseDialog,
        title = "This is UserInfoCompose",
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(pwd, onValueChange = { pwd = it }, label = { Text("PWD") })
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {}) {
                    Text("YES")
                }
                Button(onClick = {}) {
                    Text("NO")
                }
            }
        }
    }
}


class UserInfoImpl : UserInfo {

    override fun getPassphrase(): String {
        TODO("Not yet implemented")
    }

    override fun getPassword(): String {
        TODO("Not yet implemented")
    }

    override fun promptPassword(message: String?): Boolean {
        return true
    }

    override fun promptPassphrase(message: String?): Boolean {
        return true
    }

    override fun promptYesNo(message: String?): Boolean {
        return true
    }

    override fun showMessage(message: String?) {
        TODO("Not yet implemented")
    }
}