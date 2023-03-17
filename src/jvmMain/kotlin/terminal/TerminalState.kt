package terminal

class TerminalState {
    // DECKPAM
    private var applicationKeypad: Boolean = false

    // DECCKM
    private var applicationCursorKeys: Boolean = false

    // DECTCEM
    private var showCursor: Boolean = true

    fun applicationKeyPad() {
        applicationKeypad = true
    }

    fun normalKeypad() {
        applicationKeypad = false
    }

    fun applicationCursorKeys() {
        applicationCursorKeys = true
    }

    fun normalCursorKeys() {
        applicationCursorKeys = false
    }

    fun showCursor() {
        showCursor = true
    }

    fun hideCursor() {
        showCursor = false
    }

}