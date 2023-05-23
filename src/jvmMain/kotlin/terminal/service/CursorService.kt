package terminal.service

class CursorService : ICursorService {

    override var showCursor: Boolean = false
    override var cursorX: Int = 0
    override var scrollX: Int = 0
    override var cursorY: Int = 0
    override var scrollY: Int = 0


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