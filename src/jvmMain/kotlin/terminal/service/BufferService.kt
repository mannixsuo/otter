package terminal.service

import terminal.ILineBuffer
import terminal.LineBuffer


class BufferService(private val characterService: ICharacterService) : IBufferService {

    private val mainBuffer: ILineBuffer = LineBuffer(characterService)
    private val alternativeBuffer: ILineBuffer = LineBuffer(characterService)
    override var activeBuffer = mainBuffer

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