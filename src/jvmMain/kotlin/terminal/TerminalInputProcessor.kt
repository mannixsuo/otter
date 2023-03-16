package terminal

import parser.SingleCharacterFunProcessor

class TerminalInputProcessor(private val terminal: Terminal) {

    val csiProcessor = CSIProcessor(terminal)
    val singleCharacterFunProcessor = SingleCharacterFunProcessor(terminal)
    val escProcessor = EscProcessor(terminal)

}