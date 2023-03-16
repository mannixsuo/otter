package terminal

import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * container of lines reads from a terminal
 */
interface ILineBuffer {

    val lock: Lock

    /**
     * get line at index of the buffer
     */
    fun getLine(index: Int): ILine?

    /**
     * get lines that index in range
     */
    fun getLines(range: IntRange): List<ILine>

    /**
     * append line to the buffer
     */
    fun appendLine(line: ILine)

    /**
     * insert line at index
     */
    fun insertLine(index: Int, line: ILine)

    /**
     * delete line at index
     *
     * line behind the index will move up
     */
    fun deleteLine(index: Int)


    /**
     * delete lines in range
     *
     * line behind the index will move up
     */
    fun deleteLines(range: IntRange)


    fun lineCount(): Int

}


class LineBuffer : ILineBuffer {

    override val lock: Lock = ReentrantLock()

    private val _buffer = LinkedList<ILine>()


    override fun getLine(index: Int): ILine? {
        if (_buffer.size == 0) {
            return null
        }
        if (index >= _buffer.size) {
            return null
        }
        return _buffer[index]
    }

    override fun getLines(range: IntRange): List<ILine> {
        val start = 0.coerceAtLeast(range.first)
        val end = (_buffer.size - 1).coerceAtMost(range.last)
        return _buffer.slice(IntRange(start, end))
    }

    override fun appendLine(line: ILine) {
        _buffer.add(line)
    }

    override fun insertLine(index: Int, line: ILine) {
        if (index >= _buffer.size) {
            for (i in 0..index - _buffer.size) {
                _buffer.add(Line(line.maxLength()))
            }
        }
        _buffer[index] = line
    }

    override fun deleteLine(index: Int) {
        if (index in 0 until _buffer.size) {
            _buffer.removeAt(index)
        }
    }

    override fun deleteLines(range: IntRange) {

    }

    override fun lineCount(): Int {
        return _buffer.size
    }
}

