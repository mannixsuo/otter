package terminal

import parser.SingleCharacterFunProcessor
import terminal.service.*
import java.io.OutputStreamWriter

class TerminalInputProcessor(
    channelOutputStreamWriter: OutputStreamWriter,
    bufferService: IBufferService,
    characterService: ICharacterService,
    cursorService: ICursorService,
    stateService: IStateService,
    configService: IConfigService,
    tableStopService: ITableStopService
) : ITerminalInputProcessorService {

    override val csiProcessor =
        CSIProcessor(
            channelOutputStreamWriter,
            bufferService,
            characterService,
            cursorService,
            stateService,
            configService
        )

    override val singleCharacterFunProcessor = SingleCharacterFunProcessor(
        tableStopService, cursorService,
        bufferService, configService, characterService
    )
    override val escProcessor = EscProcessor(stateService)

}