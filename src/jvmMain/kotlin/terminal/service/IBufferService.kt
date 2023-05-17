package terminal.service

import terminal.ILineBuffer

/**
 * there are two buffers in terminal, one is main buffer the other one is alternative buffer
 *
 * main buffer has infinite rows
 * alternative buffer has fix number rows
 */
interface IBufferService {

    var activeBuffer: ILineBuffer

    /**
     * switch current active buffer to alternative buffer
     */
    fun switchToAlternativeBuffer()

    /**
     * switch current active buffer to main buffer
     */
    fun switchToMainBuffer()

    /**
     * check if current active buffer is main buffer
     */
    fun doesActiveBufferIsMainBuffer(): Boolean

}
