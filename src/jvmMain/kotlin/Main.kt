// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import androidx.compose.ui.zIndex
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
import terminal.AppState
import terminal.Terminal
import terminal.TerminalConfig
import ui.AppTheme
import ui.AppWindowState
import ui.session.AddSessionModal
import ui.session.SessionSelection
import ui.session.SessionSelectionState
import ui.terminal.TerminalViews
import ui.terminal.Terminals

class CoCoTerminalAppState(
    val terminals: Terminals,
    sessionList: List<Session>
) {
    var windowState: AppWindowState by mutableStateOf(AppWindowState.NORMAL)
    val sessions = sessionList.toMutableStateList()
    val sessionSelectionState by mutableStateOf(SessionSelectionState())
}

val terminalConfig = TerminalConfig()


val applicationState = AppState();


@Composable
@Preview
fun App(appState: CoCoTerminalAppState) {
    val colors = AppTheme.colors.material
    Surface(modifier = Modifier.zIndex(2F), elevation = 2.dp) {
        SessionSelection(
            appState.sessions,
            appState.sessionSelectionState,
            onAddClick = {
                appState.windowState = AppWindowState.ADD_SESSION
            }, onSessionDoubleClick = { session ->
                when (session.type) {
                    "SSH" -> {
                        session.ssh?.let {
                            val shell = JschShell(it.host, it.port, it.user, it.password)
                            val terminal = Terminal(shell, terminalConfig, applicationState, colors)
                            if (terminal.start() == 0) {
                                appState.terminals.addNewTerminal(terminal)
                                appState.sessions.add(session)
                            } else {
                                TODO()
                            }
                        }

                    }

                    "SHELL" -> {
                        session.shell?.let {
                            val localShell: Shell = LocalPty(
                                PtyProcessBuilder(arrayOf(it.command))
                                    .setInitialColumns(terminalConfig.columns)
                                    .setInitialRows(terminalConfig.rows).start()
                            )
                            val terminal = Terminal(localShell, terminalConfig, applicationState, colors)
                            if (terminal.start() == 0) {
                                appState.terminals.addNewTerminal(terminal)
                                appState.sessions.add(session)
                            } else {
                                TODO()
                            }
                        }
                    }
                }


            })
    }
    Surface(elevation = 1.dp, modifier = Modifier.zIndex(1F).padding(start = 25.dp)) {
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
    val appState = CoCoTerminalAppState(
        terminals,
        appConfig.sessionList,
    )

    MaterialTheme(
        colors = AppTheme.colors.material
    ) {
        Surface {
            when (appState.windowState) {
                AppWindowState.ADD_SESSION -> AddSessionModal(
                    addSession = {
                        appConfig.sessionList.add(it)
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
