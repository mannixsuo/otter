package terminal.service

import terminal.ILineBuffer
import terminal.LineBuffer

/**
 * save all line in terminal
 */
interface IBufferService {

    fun getActiveBuffer(): ILineBuffer

    fun switchToAlternativeBuffer()

    fun switchToMainBuffer()

    fun doesActiveBufferIsMainBuffer(): Boolean

}

class BufferService : IBufferService {

    private val mainBuffer = LineBuffer()
    private val alternativeBuffer = LineBuffer()
    private var activeBuffer = mainBuffer

    override fun getActiveBuffer(): ILineBuffer {
        return activeBuffer
    }

    override fun switchToAlternativeBuffer() {
        this.activeBuffer = alternativeBuffer
    }

    override fun switchToMainBuffer() {
        this.activeBuffer = mainBuffer
    }

    override fun doesActiveBufferIsMainBuffer(): Boolean {
        return this.activeBuffer === this.mainBuffer
    }
}