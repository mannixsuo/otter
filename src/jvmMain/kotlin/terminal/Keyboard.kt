package terminal

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key

@OptIn(ExperimentalComposeUiApi::class)
class Keyboard {
    private val keyCommandMap = HashMap<Key, CharArray>()
    private val esc = Char(0x1B)

    init {
        keyCommandMap[Key.Enter] = charArrayOf(Char(13))
        keyCommandMap[Key.DirectionUp] = charArrayOf(esc, '[', 'A')
        keyCommandMap[Key.DirectionDown] = charArrayOf(esc, '[', 'B')
        keyCommandMap[Key.DirectionRight] = charArrayOf(esc, '[', 'C')
        keyCommandMap[Key.DirectionLeft] = charArrayOf(esc, '[', 'D')
    }

    fun getKeyChar(keyCode: Key): CharArray? {
        if (keyCommandMap.containsKey(keyCode)) {
            return keyCommandMap[keyCode]
        }
        return null
    }
}