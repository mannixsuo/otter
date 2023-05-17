package ui.terminal

import terminal.Terminal
import ui.SingleSelection

class Terminals {

    private val selection = SingleSelection()

    val terminals: MutableList<Terminal> = mutableListOf()

    var active: Int = 0

    val activeTerminal: Terminal? get() = selection.selected as Terminal?

    fun addNewTerminal(newTerminal: Terminal) {
        selection.selected = newTerminal
        terminals.add(newTerminal)
        newTerminal.close = fun() {
            newTerminal.stop()
            terminals.remove(newTerminal)
            if (newTerminal.isActive) {
                if (terminals.isNotEmpty()) {
                    terminals[0].activate()
                }
            }
        }
        newTerminal.selection = selection
        active = active++
    }
}
