package terminal

import terminal.service.ICharacterService
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max

class AlternativeLineBuffer(
    private val characterService: ICharacterService,
    private val maxColumns: Int,
    private val maxRows: Int
) : AbstractLineBuffer(ReentrantLock()) {


    private val _buffer: MutableList<ILine> = ArrayList(maxRows)

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
        if (_buffer.size >= maxRows) {
            _buffer[_buffer.size - 1] = line
        } else {
            _buffer.add(line)
        }
    }

    override fun insertBufferLine(index: Int, line: ILine) {
        if (index >= _buffer.size) {
            _buffer[_buffer.size - 1] = line
        } else {
            _buffer[index] = line
        }
    }

    override fun deleteBufferLine(index: Int) {
        if (index < 0) {
            return
        }
        if (index < _buffer.size) {
            _buffer[_buffer.size - 1] = Line(maxColumns, characterService.createEmptyCell())
        }
    }

    override fun deleteBufferLines(range: IntRange) {
        val start = range.first
        val endInclusive = max(range.last, maxRows)
        val emptyLine = Line(maxColumns, characterService.createEmptyCell())
        for (i in start..endInclusive) {
            _buffer[i] = emptyLine
        }
    }

    override fun bufferLineCount(): Int {
        return maxRows
    }

}