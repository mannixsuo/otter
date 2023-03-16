package ui.terminal

import terminal.Terminal
import ui.ActiveTerminalScreen
import ui.SingleSelection

class Terminals {

    private val selection = SingleSelection()

    var initialized: Boolean = false

    val activeTerminalScreen = ActiveTerminalScreen()

    val terminals: ArrayList<Terminal> = ArrayList()

    var active: Int = 0

    val activeTerminal: Terminal? get() = selection.selected as Terminal?


    fun addNewTerminal(newTerminal: Terminal) {
        selection.selected = newTerminal
        terminals.add(newTerminal)
        newTerminal.viewInitialized = fun(): Boolean {
            return initialized
        }
        newTerminal.close = fun() {
            newTerminal.stop()
            terminals.remove(newTerminal)

            if (newTerminal.isActive) {
                terminals[0].activate()
            }
        }

        newTerminal.onLineChange = fun() {
            if (!initialized) {
                return
            }
            activeTerminalScreen.screenLines = newTerminal.bufferService.getActiveBuffer()
                .getLines(IntRange(newTerminal.scrollY, newTerminal.scrollY + newTerminal.terminalConfig.rows))
            activeTerminalScreen.cursorX = newTerminal.cursorX
            activeTerminalScreen.cursorY = newTerminal.cursorY
        }
        newTerminal.selection = selection
        active = active++
    }
}
