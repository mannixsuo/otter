package ui.terminal

import CoCoTerminalAppState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TerminalViews(model: CoCoTerminalAppState) {

    Box(modifier = Modifier.padding(2.dp)) {
        Column {
            TerminalTablesView(model.terminals)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                val terminal = model.terminals.activeTerminal
                if (terminal == null) {
                    Text("Open")
                } else {
                    TerminalView(terminal.version,terminal.configService, terminal.bufferService, terminal.cursorService)
                }

            }
        }
    }
}