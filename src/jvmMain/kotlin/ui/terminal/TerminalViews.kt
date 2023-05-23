package ui.terminal

import CoCoTerminalAppState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import terminal.ILine

@Composable
fun TerminalViews(model: CoCoTerminalAppState) {
    var timer by remember { mutableStateOf(0) }
    var screenLines: List<ILine>? by remember { mutableStateOf(null, neverEqualPolicy()) }

    LaunchedEffect(timer) {
        while (true) {
            delay(200)
            val activeTerminal = model.terminals.activeTerminal
            screenLines = activeTerminal?.bufferService?.activeBuffer?.getLines(
                IntRange(
                    activeTerminal.cursorService.scrollY,
                    activeTerminal.cursorService.scrollY + activeTerminal.configService.maxRows
                )
            )
            timer %= 100
        }
    }
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
                    TerminalView(
                        { terminal.focused = true },
                        { terminal.focused = false },
                        screenLines ?: emptyList(),
                        terminal.configService,
                        terminal.bufferService,
                        terminal.cursorService
                    )
                }

            }
        }
    }
}