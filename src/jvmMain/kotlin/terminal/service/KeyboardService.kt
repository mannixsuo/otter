package terminal.service

import androidx.compose.ui.input.key.*
import java.awt.event.KeyEvent.*
import java.io.OutputStreamWriter

class KeyboardService : IKeyboardService {
    private val keyCommandMap = HashMap<Key, CharArray>()
    private val esc = Char(0x1B)

    init {
        keyCommandMap[Key(VK_ENTER)] = charArrayOf(Char(13))
        keyCommandMap[Key(VK_UP)] = charArrayOf(esc, '[', 'A')
        keyCommandMap[Key(VK_DOWN)] = charArrayOf(esc, '[', 'B')
        keyCommandMap[Key(VK_RIGHT)] = charArrayOf(esc, '[', 'C')
        keyCommandMap[Key(VK_LEFT)] = charArrayOf(esc, '[', 'D')
    }

    fun getKeyChar(keyCode: Key): CharArray? {
        if (keyCommandMap.containsKey(keyCode)) {
            return keyCommandMap[keyCode]
        }
        return null
    }

    override fun onKeyEvent(event: KeyEvent, outputStreamWriter: OutputStreamWriter) {
        if (event.type == KeyEventType.KeyDown) {
            when (event.key) {
                Key(
                    VK_SHIFT,
                    KEY_LOCATION_LEFT
                ), Key(
                    VK_SHIFT,
                    KEY_LOCATION_RIGHT
                ), Key(VK_CONTROL, KEY_LOCATION_LEFT), Key(
                    VK_CONTROL,
                    KEY_LOCATION_RIGHT
                ), Key(VK_ALT, KEY_LOCATION_LEFT), Key(
                    VK_ALT,
                    KEY_LOCATION_RIGHT
                ) -> return
            }
            val toInt = event.utf16CodePoint
            val keyChar = getKeyChar(event.key)
            if (keyChar == null) {
                outputStreamWriter.write(toInt)
            } else {
                outputStreamWriter.write(keyChar)
            }
            outputStreamWriter.flush()
        }
    }

    override fun getKeyChar(event: KeyEvent): CharArray? {
        if (keyCommandMap.containsKey(event.key)) {
            return keyCommandMap[event.key]
        }
        return null
    }
}