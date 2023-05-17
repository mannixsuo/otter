package terminal.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CursorService : ICursorService {

    override var cursorX: Int by mutableStateOf(0)
    override var scrollX: Int by mutableStateOf(0)
    override var cursorY: Int by mutableStateOf(0)
    override var scrollY: Int by mutableStateOf(0)


    override fun forward(count: Int) {
        cursorX += count
    }

    override fun back(count: Int) {
        cursorX -= count
    }

    override fun up(count: Int) {
        cursorY -= count
    }

    override fun down(count: Int) {
        cursorY += count
    }

    override fun getAbsoluteRowNumber(): Int {
        return scrollY + cursorY
    }

    override fun getAbsoluteColumnNumber(): Int {
        return scrollX + cursorX
    }

    override fun restrictCursor() {
        TODO("Not yet implemented")
    }
}