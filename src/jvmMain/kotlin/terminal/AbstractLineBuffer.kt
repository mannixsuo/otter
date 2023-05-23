package terminal

import java.util.concurrent.locks.Lock

abstract class AbstractLineBuffer(override val lock: Lock) : ILineBuffer {
    abstract fun getBufferLine(index: Int): ILine?
    abstract fun getBufferLines(range: IntRange): List<ILine>
    abstract fun getBufferAllLines(): List<ILine>

    abstract fun appendBufferLine(line: ILine)
    abstract fun insertBufferLine(index: Int, line: ILine)

    abstract fun deleteBufferLine(index: Int)
    abstract fun deleteBufferLines(range: IntRange)

    abstract fun bufferLineCount(): Int
    override fun getLine(index: Int): ILine? {
        try {
            lock.lock()
            return getBufferLine(index)
        } finally {
            lock.unlock()
        }
    }

    override fun getLines(range: IntRange): List<ILine> {
        try {
            lock.lock()
            return getBufferLines(range)
        } finally {
            lock.unlock()
        }
    }

    override fun getAllLines(): List<ILine> {
        try {
            lock.lock()
            return getBufferAllLines()
        } finally {
            lock.unlock()
        }
    }

    override fun appendLine(line: ILine) {
        try {
            lock.lock()
            return appendBufferLine(line)
        } finally {
            lock.unlock()
        }
    }

    override fun insertLine(index: Int, line: ILine) {
        try {
            lock.lock()
            return insertBufferLine(index, line)
        } finally {
            lock.unlock()
        }
    }

    override fun deleteLine(index: Int) {
        try {
            lock.lock()
            return deleteBufferLine(index)
        } finally {
            lock.unlock()
        }
    }

    override fun deleteLines(range: IntRange) {
        try {
            lock.lock()
            return deleteBufferLines(range)
        } finally {
            lock.unlock()
        }
    }

    override fun lineCount(): Int {
        try {
            lock.lock()
            return bufferLineCount()
        } finally {
            lock.unlock()
        }
    }
}