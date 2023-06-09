package terminal.service

class StateService : IStateService {
    // DECKPAM
    private var applicationKeypad: Boolean = false

    // DECCKM
    private var applicationCursorKeys: Boolean = false

    // DECTCEM
    private var showCursor: Boolean = true

    //
    private var bracketedPastMode = false

    override fun applicationKeyPad() {
        applicationKeypad = true
    }

    override fun normalKeypad() {
        applicationKeypad = false
    }

    override fun applicationCursorKeys() {
        applicationCursorKeys = true
    }

    override fun normalCursorKeys() {
        applicationCursorKeys = false
    }

    override fun showCursor() {
        showCursor = true
    }

    override fun hideCursor() {
        showCursor = false
    }

    override fun bracketedPastModeReset() {
        bracketedPastMode = false
    }

    override fun bracketedPastModeSet() {
        bracketedPastMode = true
    }
}