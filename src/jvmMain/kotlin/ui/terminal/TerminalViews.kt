package ui.terminal

import CoCoTerminalAppState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.AppTheme

@Composable
fun TerminalViews(model: CoCoTerminalAppState) {

    Box {
        Column {
            TerminalTablesView(model.terminals)
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(AppTheme.colors.material.background)
            ) {
                val terminal = model.terminals.activeTerminal
                if (terminal == null) {
                    Text("Open")
                } else {
                    TerminalView(terminal)
                }

            }
        }
    }
}