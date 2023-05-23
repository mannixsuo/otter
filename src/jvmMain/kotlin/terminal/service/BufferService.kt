package terminal.service

import terminal.AbstractLineBuffer
import terminal.AlternativeLineBuffer
import terminal.ILineBuffer
import terminal.LineBuffer


class BufferService(private val characterService: ICharacterService, configService: IConfigService) : IBufferService {

    private val mainBuffer: AbstractLineBuffer =
        LineBuffer(characterService, configService.maxColumns, configService.maxRows)

    private val alternativeBuffer: AbstractLineBuffer =
        AlternativeLineBuffer(characterService, configService.maxColumns, configService.maxRows)

    override var activeBuffer: ILineBuffer = mainBuffer

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