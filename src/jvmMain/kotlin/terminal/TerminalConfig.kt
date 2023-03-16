package terminal

import config.Session
import terminal.buffer.Theme
import terminal.buffer.defaultTheme

class TerminalConfig {

    val theme: Theme = defaultTheme
    val columns = 120
    val rows = 30

    val sessions: List<Session> = emptyList()

}