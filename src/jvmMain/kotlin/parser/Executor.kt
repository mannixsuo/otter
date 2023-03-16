package parser

import terminal.Terminal

interface Executor {
    fun execute(terminal: Terminal)
}

