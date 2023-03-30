// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.pty4j.PtyProcessBuilder
import config.Session
import config.readConfigFromFile
import config.writeConfigToFile
import org.slf4j.LoggerFactory
import shell.JschShell
import shell.LocalPty
import shell.Shell
import terminal.Cell
import terminal.Terminal
import terminal.TerminalConfig
import ui.AppTheme
import ui.AppWindowState
import ui.layout.SplitterState
import ui.layout.VerticalSplittableForTwoElements
import ui.session.AddSessionModal
import ui.session.SessionSelection
import ui.session.SessionSelectionState
import ui.terminal.TerminalViews
import ui.terminal.Terminals

class CoCoTerminalAppState(
    val terminals: Terminals, val splitterState: SplitterState, initialSessions: List<Session>
) {
    var windowState: AppWindowState by mutableStateOf(AppWindowState.NORMAL)
    val sessions = initialSessions.toMutableStateList()
    val sessionSelectionState by mutableStateOf(SessionSelectionState())
}

val terminalConfig = TerminalConfig()

val localShell: Shell = LocalPty(
    PtyProcessBuilder(arrayOf("powershell")).setInitialColumns(terminalConfig.columns)
        .setInitialRows(terminalConfig.rows).start()
)
val terminal = Terminal(localShell, terminalConfig)

val emptyCell = Cell(
    char = Char(0),
    bg = AppTheme.colors.material.background,
    fg = AppTheme.colors.material.primary,
    bold = false,
    italic = false
)

@Composable
@Preview
fun App(appState: CoCoTerminalAppState) {

    VerticalSplittableForTwoElements(Modifier.fillMaxSize(),
        appState.splitterState,
        onResize = {
            appState.sessionSelectionState.expandedSize =
                (appState.sessionSelectionState.expandedSize + it)
                    .coerceAtLeast(appState.sessionSelectionState.expandedSizeMin)
        }) {
        SessionSelection(appState.sessions,
            appState.sessionSelectionState,
            onAddClick = {
                appState.windowState = AppWindowState.ADD_SESSION
            }, onSessionDoubleClick = { it ->
                val shell = JschShell(it.host, it.port, it.user, it.password)
                val terminal = Terminal(shell, terminalConfig)
                if (terminal.start() == 0) {
                    appState.terminals.addNewTerminal(terminal)
                    appState.sessions.add(it)
                } else {
                    TODO()
                }
            })
        TerminalViews(appState)

    }

}


val terminals = Terminals()

val appConfig = readConfigFromFile()

fun main() = singleWindowApplication(
    title = "Otter Terminal",
    onKeyEvent = { key ->
        terminals.activeTerminal?.onKeyEvent(key)
        true
    },
    state = WindowState(width = 1280.dp, height = 768.dp),
    icon = BitmapPainter(useResource("ic_launcher.png", ::loadImageBitmap)),
) {
    val appState = CoCoTerminalAppState(terminals, SplitterState(), appConfig.sessions)
    terminals.addNewTerminal(terminal)
    terminal.start()
    MaterialTheme(
        colors = AppTheme.colors.material
    ) {
        Surface {
            when (appState.windowState) {
                AppWindowState.ADD_SESSION -> AddSessionModal(onConfirmClick = {
                    appConfig.sessions.add(it)
                    writeConfigToFile(appConfig)
                    appState.sessions.add(it)
                }) { appState.windowState = AppWindowState.NORMAL }
                else -> App(appState)
            }
        }

    }
    val parentLogger = LoggerFactory.getLogger("kotlin") as Logger
    parentLogger.level = Level.DEBUG
}
