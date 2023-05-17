package terminal.service

/**
 * control state of a terminal
 */
interface IStateService {
    /**
     * active application key pad mode
     * DECKPAM
     */
    fun applicationKeyPad()

    /**
     * active normal key pad mode
     * DECKPAM
     */
    fun normalKeypad()

    /**
     * active application cursor keys mode
     * DECCKM
     */
    fun applicationCursorKeys()

    /**
     * active normal cursor keys mode
     * DECCKM
     */
    fun normalCursorKeys()

    /**
     * show cursor
     * DECTCEM
     */
    fun showCursor()

    /**
     * hide cursor
     * DECTCEM
     */
    fun hideCursor()
}