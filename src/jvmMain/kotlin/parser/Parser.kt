package parser

import character.TransitionTable
import terminal.service.IConfigService
import terminal.service.ITerminalInputProcessorService
import terminal.service.ITerminalOutputProcessorService
import java.io.OutputStreamWriter
import java.util.*

class Parser(
    private val configService: IConfigService,
    private val transitionTable: TransitionTable,
    private val terminalInputProcessor: ITerminalInputProcessorService,
    private val terminalOutputProcessorService: ITerminalOutputProcessorService,
    channelOutputStreamWriter: OutputStreamWriter
) {

    private var currentState = ParserState.GROUND
    private var currentAction = ParserAction.PRINT
    private var params = Params()
    private var collect = Stack<Char>()
    private val oscHandler = OSCHandler(configService, channelOutputStreamWriter)
    private var dcsHandler = DCSHandler()
    private val csiHandler = CsiHandler(terminalInputProcessor.csiProcessor)
    private val escHandler = EscHandler(terminalInputProcessor.escProcessor)


    fun onCharArray(charArray: CharArray) {
        charArray.forEach { onChar(it) }
    }

    private fun onChar(code: Char) {
        val (nextAction, nextState) = transitionTable.queryTable(code.code, currentState)
        when (nextAction) {
            ParserAction.IGNORE, ParserAction.ERROR -> {}
            ParserAction.PRINT -> {
                terminalOutputProcessorService.print(code)
            }

            ParserAction.EXECUTE -> {
                terminalInputProcessor.singleCharacterFunProcessor.handleCode(code.code)
            }

            ParserAction.CLEAR -> {
                params.reset()
                collect.clear()
            }

            ParserAction.OSC_START -> {
                oscHandler.reset()
            }

            ParserAction.OSC_PUT -> {
                oscHandler.put(code.code)
            }

            ParserAction.OSC_END -> {
                oscHandler.finish()
            }

            ParserAction.CSI_DISPATCH -> {
                csiHandler.csiDispatch(collect, params, code.code)
            }

            ParserAction.PARAM -> {
                params.put(code.code)
            }

            ParserAction.COLLECT -> {
                collect.push(code)
            }

            ParserAction.ESC_DISPATCH -> {
                escHandler.escDispatch(params)
            }

            ParserAction.DCS_HOOK -> {
                TODO()
            }

            ParserAction.DCS_PUT -> {
                dcsHandler.put(code.code)
            }

            ParserAction.DCS_UNHOOK -> {
                dcsHandler.unHook()
            }
        }
        currentState = nextState
        currentAction = nextAction
    }


}