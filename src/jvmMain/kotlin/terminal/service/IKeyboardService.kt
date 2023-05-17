package terminal.service

import androidx.compose.ui.input.key.KeyEvent
import java.io.OutputStreamWriter

/**
 * terminal keyboard service
 */
interface IKeyboardService {

    /**
     * handle key event of terminal and send translated key to outputStreamWriter
     */
    fun onKeyEvent(event: KeyEvent, outputStreamWriter: OutputStreamWriter)

    /**
     * get key char of a key
     */
    fun getKeyChar(event: KeyEvent): CharArray?

}