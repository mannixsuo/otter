package terminal

import terminal.service.ICharacterService
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock


class LineBuffer(private val characterService: ICharacterService) : ILineBuffer {

    override val lock: Lock = ReentrantLock()

    private val _buffer: MutableList<ILine> = mutableListOf()

    override fun getLine(index: Int): ILine? {
        try {
            lock.lock()
            if (_buffer.size == 0) {
                return null
            }
            if (index >= _buffer.size) {
                return null
            }
            return _buffer[index]
        } finally {
            lock.unlock()
        }
    }

    override fun getAllLines(): List<ILine> {
        try {
            lock.lock()
            return _buffer
        } finally {
            lock.unlock()
        }
    }

    override fun getLines(range: IntRange): List<ILine> {
        try {
            lock.lock()
            val start = 0.coerceAtLeast(range.first)
            val end = (_buffer.size - 1).coerceAtMost(range.last - 1)
            return _buffer.slice(IntRange(start, end))
        } finally {
            lock.unlock()
        }

    }

    override fun appendLine(line: ILine) {
        try {
            lock.lock()
            _buffer.add(line)
        } finally {
            lock.unlock()
        }
    }

    override fun insertLine(index: Int, line: ILine) {
        try {
            lock.lock()
            if (index >= _buffer.size) {
                for (i in 0..index - _buffer.size) {
                    _buffer.add(Line(line.maxLength(), characterService.createEmptyCell()))
                }
            }
            _buffer[index] = line
        } finally {
            lock.unlock()
        }
    }

    override fun deleteLine(index: Int) {
        try {
            lock.lock()
            if (index in 0 until _buffer.size) {
                _buffer.removeAt(index)
            }
        } finally {
            lock.unlock()
        }
    }

    override fun deleteLines(range: IntRange) {

    }

    override fun lineCount(): Int {
        return _buffer.size
    }
}

