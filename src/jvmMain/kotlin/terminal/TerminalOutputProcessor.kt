package terminal

import org.slf4j.LoggerFactory
import terminal.service.*

class TerminalOutputProcessor(
    private val bufferService: IBufferService,
    private val configService: IConfigService,
    private val characterService: ICharacterService,
    private val cursor: ICursorService
) : ITerminalOutputProcessorService {
    private val logger = LoggerFactory.getLogger(TerminalOutputProcessor::class.java)

    override fun print(code: Int) {
        if (code == 0xFFFF) {
            return
        }
        val activeBuffer = bufferService.activeBuffer
        try {
            activeBuffer.lock.lock()

            val absoluteRowNumber = cursor.getAbsoluteRowNumber()

            var lineAtCurrentCursor =
                activeBuffer.getLine(absoluteRowNumber)

            if (lineAtCurrentCursor == null) {
                lineAtCurrentCursor = Line(configService.maxColumns, characterService.createEmptyCell())

                activeBuffer.insertLine(
                    absoluteRowNumber,
                    lineAtCurrentCursor
                )
            }
            val absoluteColumnNumber = cursor.getAbsoluteColumnNumber()
            lineAtCurrentCursor.replaceCell(absoluteColumnNumber, characterService.buildCell(code))
            if (logger.isDebugEnabled) {
                logger.debug("write {} to ({},{})", Char(code), absoluteRowNumber, absoluteColumnNumber)
            }
            cursor.forward(1)
        } finally {
            activeBuffer.lock.unlock()
        }
    }
}