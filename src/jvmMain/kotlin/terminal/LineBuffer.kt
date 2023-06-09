package terminal

import terminal.service.ICharacterService
import java.util.concurrent.locks.ReentrantLock

class LineBuffer(
    private val characterService: ICharacterService,
    private val maxColumns: Int, private val maxRows: Int
) : AbstractLineBuffer(ReentrantLock()) {

    private val _buffer: MutableList<ILine> = mutableListOf()

    override fun getBufferLine(index: Int): ILine? {
        if (_buffer.size == 0) {
            return null
        }
        if (index >= _buffer.size) {
            return null
        }
        return _buffer[index]
    }

    override fun getBufferLines(range: IntRange): List<ILine> {
        val start = 0.coerceAtLeast(range.first)
        val end = (_buffer.size - 1).coerceAtMost(range.last - 1)
        return _buffer.slice(IntRange(start, end))
    }

    override fun getBufferAllLines(): List<ILine> {
        return _buffer
    }

    override fun appendBufferLine(line: ILine) {
        _buffer.add(line)
    }

    override fun insertBufferLine(index: Int, line: ILine) {
        if (index >= _buffer.size) {
            for (i in 0..index - _buffer.size) {
                val emptyLine = Line(line.maxLength(), characterService.createEmptyCell())
                _buffer.add(emptyLine)
            }
        }
        _buffer[index] = line
    }

    override fun deleteBufferLine(index: Int) {
        if (index in 0 until _buffer.size) {
            _buffer.removeAt(index)
        }
    }

    override fun deleteBufferLines(range: IntRange) {
        val start = 0.coerceAtLeast(range.first)
        val end = (_buffer.size - 1).coerceAtMost(range.last - 1)
        val emptyLine = Line(maxColumns, characterService.createEmptyCell())
        for (i in start..end) {
            _buffer[i] = emptyLine
        }
    }

    override fun bufferLineCount(): Int {
        return _buffer.size

    }

}

