package terminal.service

/**
 * save and manipulate cursor info
 */
interface ICursorService {
    var cursorX: Int
    var scrollX: Int
    var cursorY: Int
    var scrollY: Int
    fun forward(count: Int)

    fun back(count: Int)

    fun up(count: Int)

    fun down(count: Int)


    fun getAbsoluteRowNumber(): Int

    fun getAbsoluteColumnNumber(): Int

    /**
     * reset cursor status to avoid cursor in incorrect position
     */
    fun restrictCursor(): Unit
}