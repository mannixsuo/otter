package terminal

import terminal.service.IStateService

class EscProcessor(private val stateService: IStateService) {

    // The auxiliary keypad keys will transmit control sequences as defined in Tables 3-7 and 3-8.
    fun applicationKeypad() {
        stateService.applicationKeyPad()
    }

    fun normalKeypad() {
        stateService.normalKeypad()
    }

}