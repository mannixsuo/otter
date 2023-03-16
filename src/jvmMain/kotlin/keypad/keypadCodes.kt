package keypad

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key

class keypadCodes {

    data class KeypadCode(val normal: CharArray, val application: CharArray)
    @OptIn(ExperimentalComposeUiApi::class)
    companion object{
        private val esc = Char(0x1B)

        @OptIn(ExperimentalComposeUiApi::class)
        val KeypadCodeMap = HashMap<Key,KeypadCode>()

        init {
            KeypadCodeMap[Key.DirectionUp] =  KeypadCode(charArrayOf(esc, '[', 'A'), charArrayOf(esc,'O','A'))
            KeypadCodeMap[Key.DirectionDown] =  KeypadCode(charArrayOf(esc, '[', 'B'), charArrayOf(esc,'O','B'))
            KeypadCodeMap[Key.DirectionRight] =  KeypadCode(charArrayOf(esc, '[', 'C'), charArrayOf(esc,'O','C'))
            KeypadCodeMap[Key.DirectionLeft] =  KeypadCode(charArrayOf(esc, '[', 'D'), charArrayOf(esc,'O','D'))
            KeypadCodeMap[Key.Home] =  KeypadCode(charArrayOf(esc, '[', 'H'), charArrayOf(esc,'O','H'))
            KeypadCodeMap[Key.MoveEnd] =  KeypadCode(charArrayOf(esc, '[', 'F'), charArrayOf(esc,'O','F'))
        }
    }


}