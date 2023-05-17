package terminal

import androidx.compose.ui.input.key.KeyEvent

interface ITerminal {
    /**
     * start this terminal
     */
    fun start(): Int

    /**
     * stop this terminal
     */
    fun stop(): Int

    /**
     * key event
     */
    fun onKeyEvent(event: KeyEvent)
}