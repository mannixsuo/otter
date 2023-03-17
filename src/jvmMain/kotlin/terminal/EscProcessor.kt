package terminal

class EscProcessor(private val terminal: Terminal) {

    // The auxiliary keypad keys will transmit control sequences as defined in Tables 3-7 and 3-8.
    fun applicationKeypad() {
        terminal.state.applicationKeyPad()
    }

    fun normalKeypad() {
        terminal.state.normalKeypad()
    }

}