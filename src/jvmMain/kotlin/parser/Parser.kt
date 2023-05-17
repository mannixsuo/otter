package parser

import character.TransitionTable
import terminal.service.IConfigService
import terminal.service.ITerminalInputProcessorService
import terminal.service.ITerminalOutputProcessorService
import java.util.*

class Parser(
    private val configService: IConfigService,
    private val transitionTable: TransitionTable,
    private val terminalInputProcessor: ITerminalInputProcessorService,
    private val terminalOutputProcessorService: ITerminalOutputProcessorService
) {

    private var currentState = ParserState.GROUND
    private var currentAction = ParserAction.PRINT
    private var params = Params()
    private var collect = Stack<Char>()
    private val oscHandler = OSCHandler(configService)
    private var dcsHandler = DCSHandler()
    private val csiHandler = CsiHandler(terminalInputProcessor.csiProcessor)
    private val escHandler = EscHandler(terminalInputProcessor.escProcessor)


    fun onCharArray(charArray: CharArray) {
        charArray.forEach { onChar(it.code) }
    }

    private fun onChar(code: Int) {
        val (nextAction, nextState) = transitionTable.queryTable(code, currentState)
        when (nextAction) {
            ParserAction.IGNORE, ParserAction.ERROR -> {}
            ParserAction.PRINT -> {
                terminalOutputProcessorService.print(code)
            }

            ParserAction.EXECUTE -> {
                terminalInputProcessor.singleCharacterFunProcessor.handleCode(code)
            }

            ParserAction.CLEAR -> {
                params.reset()
                collect.clear()
            }

            ParserAction.OSC_START -> {
                oscHandler.reset()
            }

            ParserAction.OSC_PUT -> {
                oscHandler.put(code)
            }

            ParserAction.OSC_END -> {
                oscHandler.finish()
            }

            ParserAction.CSI_DISPATCH -> {
                csiHandler.csiDispatch(collect, params, code)
            }

            ParserAction.PARAM -> {
                params.put(code)
            }

            ParserAction.COLLECT -> {
                collect.push(code.toChar())
            }

            ParserAction.ESC_DISPATCH -> {
                escHandler.escDispatch(params)
            }

            ParserAction.DCS_HOOK -> {
                TODO()
            }

            ParserAction.DCS_PUT -> {
                dcsHandler.put(code)
            }

            ParserAction.DCS_UNHOOK -> {
                dcsHandler.unHook()
            }
        }
        currentState = nextState
        currentAction = nextAction
    }


}