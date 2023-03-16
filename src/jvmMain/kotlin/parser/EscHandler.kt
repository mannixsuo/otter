package parser

import terminal.EscProcessor

class EscHandler(val escProcessor: EscProcessor) {
    data class EscCommand(val first: Char, val second: Char?, val final: Char) {
        fun key(): Int {
            return generateKey(first, second, final)
        }
    }

    fun escDispatch(first: Char, second: Char?, final: Char) {

        when (final) {
            '=' -> escProcessor.applicationKeypad()
            '>' -> escProcessor.normalKeypad()
        }
    }
}