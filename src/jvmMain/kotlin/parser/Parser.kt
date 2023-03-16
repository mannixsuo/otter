package parser

import character.NON_ASCII_PRINTABLE
import character.TransitionTable
import org.slf4j.LoggerFactory
import terminal.Terminal
import terminal.TerminalInputProcessor
import java.util.*

class Parser(private val terminal: Terminal) {

    private val logger = LoggerFactory.getLogger(Parser::class.java)


    var currentState = ParserState.GROUND
    var currentAction = ParserAction.PRINT
    private var params = Params()
    private var collect = Stack<Char>()
    private val transitionTable = TransitionTable(4096)
    private val oscHandler = OSCHandler()
    private var dcsHandler = DCSHandler()
    private val terminalInputProcessor = TerminalInputProcessor(terminal)
    private val csiHandler = CsiHandler(terminalInputProcessor)
    private val escHandler = EscHandler(terminalInputProcessor.escProcessor)

    init {
        initTransitionTable()
    }

    private fun initTransitionTable() {
        val blueprint = Array(256) { it }
        // executable characters
        var executable = blueprint.sliceArray(IntRange(0x00, 0x1a - 1))
        executable = executable.plus(blueprint.sliceArray(IntRange(0x1c, 0x20 - 1)))
        with(transitionTable) {
            // default any error action will turn to ground state
            setDefault(ParserAction.ERROR, ParserState.GROUND)
            // printable character
            addRange(0x20, 0x7f, ParserState.GROUND, ParserAction.PRINT, ParserState.GROUND)
            // global anywhere rules
            for (parserState in ParserState.values()) {
                // anywhere -> GROUND
                // 18,1A / execute
                // 80-8F,91-97,99,9A / execute
                addMany(arrayOf(0x18, 0x1a, 0x99, 0x9a), parserState, ParserAction.EXECUTE, ParserState.GROUND)
                addRange(0x80, 0x8f, parserState, ParserAction.EXECUTE, ParserState.GROUND)
                // 9C / (no action)
                add(0x9c, parserState, ParserAction.IGNORE, ParserState.GROUND)
                // anywhere -> sos/pm/apc string
                // 98,9E,9F
                addMany(arrayOf(0x98, 0x9e, 0x9f), parserState, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
                // anywhere -> escape
                // 1B
                add(0x1b, parserState, ParserAction.CLEAR, ParserState.ESCAPE)
                // anywhere -> dcs entry
                // 90
                add(0x90, parserState, ParserAction.CLEAR, ParserState.ESCAPE)
                // anywhere -> osc string
                // 9D
                add(0x9d, parserState, ParserAction.OSC_START, ParserState.OSC_STRING)
                // anywhere -> csi entry
                // 9B
                add(0x9b, parserState, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            }
            // rules for executables and 7f
            addMany(executable, ParserState.GROUND, ParserAction.EXECUTE, ParserState.GROUND)
            addMany(executable, ParserState.ESCAPE, ParserAction.EXECUTE, ParserState.ESCAPE)
            add(0x7f, ParserState.ESCAPE, ParserAction.IGNORE, ParserState.ESCAPE)
            addMany(executable, ParserState.OSC_STRING, ParserAction.IGNORE, ParserState.OSC_STRING)
            // csi
            add(0x5b, ParserState.ESCAPE, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            add(0x3b, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x30, 0x39, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x3c, 0x3f, ParserState.CSI_ENTRY, ParserAction.COLLECT, ParserState.CSI_PARAM)

            // escape -> escape intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.ESCAPE, ParserAction.COLLECT, ParserState.ESCAPE_INTERMEDIATE)
            // event 00-17,19,1C-1F / execute
            addRange(0x00, 0x17, ParserState.ESCAPE_INTERMEDIATE, ParserAction.EXECUTE, ParserState.ESCAPE_INTERMEDIATE)
            addRange(0x1c, 0x1f, ParserState.ESCAPE_INTERMEDIATE, ParserAction.EXECUTE, ParserState.ESCAPE_INTERMEDIATE)
            add(0x19, ParserState.ESCAPE_INTERMEDIATE, ParserAction.EXECUTE, ParserState.ESCAPE_INTERMEDIATE)
            // event 20-2F / collect
            addRange(0x20, 0x2f, ParserState.ESCAPE_INTERMEDIATE, ParserAction.COLLECT, ParserState.ESCAPE_INTERMEDIATE)
            // event 7F / ignore
            add(0x7f, ParserState.ESCAPE_INTERMEDIATE, ParserAction.IGNORE, ParserState.ESCAPE_INTERMEDIATE)
            // 30-7E / esc_dispatch
            addRange(0x30, 0x7e, ParserState.ESCAPE_INTERMEDIATE, ParserAction.ESC_DISPATCH, ParserState.GROUND)

            // escape -> ground
            // 30-4F,51-57,59,5A, 5C,60-7E / esc_dispatch
            addRange(0x30, 0x4f, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            addRange(0x51, 0x57, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            add(0x59, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            add(0x5a, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            add(0x5c, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)
            addRange(0x60, 0x7e, ParserState.ESCAPE, ParserAction.ESC_DISPATCH, ParserState.GROUND)

            // escape -> sos/pm/apc string
            // 58,5E,5F
            add(0x58, ParserState.ESCAPE, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            // event 00-17,19,1C-1F,20-7F / ignore
            addRange(0x00, 0x17, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            add(0x19, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            addRange(0x1c, 0x1f, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)
            addRange(0x20, 0x7f, ParserState.SOS_PM_APC_STRING, ParserAction.IGNORE, ParserState.SOS_PM_APC_STRING)

            // escape -> csi entry
            // 5B
            add(0x5b, ParserState.ESCAPE, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            // event 00-17,19,1C-1F / execute
            add(0x19, ParserState.CSI_ENTRY, ParserAction.EXECUTE, ParserState.CSI_ENTRY)
            addRange(0x00, 0x17, ParserState.CSI_ENTRY, ParserAction.EXECUTE, ParserState.CSI_ENTRY)
            addRange(0x1c, 0x1f, ParserState.CSI_ENTRY, ParserAction.EXECUTE, ParserState.CSI_ENTRY)
            add(0x7f, ParserState.CSI_ENTRY, ParserAction.IGNORE, ParserState.CSI_ENTRY)

            // csi entry -> csi param
            // 30-39,3B / param
            // 3C-3F / collect
            add(0x3b, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x30, 0x39, ParserState.CSI_ENTRY, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x3c, 0x3f, ParserState.CSI_ENTRY, ParserAction.COLLECT, ParserState.CSI_PARAM)
            // csi param
            // event 00-17,19,1C-1F / execute
            add(0x19, ParserState.CSI_PARAM, ParserAction.EXECUTE, ParserState.CSI_PARAM)
            addRange(0x00, 0x17, ParserState.CSI_PARAM, ParserAction.EXECUTE, ParserState.CSI_PARAM)
            addRange(0x1c, 0x1f, ParserState.CSI_PARAM, ParserAction.EXECUTE, ParserState.CSI_PARAM)
            // event 30-39,3B / param
            add(0x3b, ParserState.CSI_PARAM, ParserAction.PARAM, ParserState.CSI_PARAM)
            addRange(0x30, 0x39, ParserState.CSI_PARAM, ParserAction.PARAM, ParserState.CSI_PARAM)
            // event 7F / ignore
            add(0x7f, ParserState.CSI_PARAM, ParserAction.IGNORE, ParserState.CSI_PARAM)

            // csi param -> ground
            // 40-7E / csi_dispatch
            addRange(0x40, 0x7e, ParserState.CSI_PARAM, ParserAction.CSI_DISPATCH, ParserState.GROUND)

            // csi param -> csi intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.CSI_PARAM, ParserAction.COLLECT, ParserState.CSI_INTERMEDIATE)
            // event 00-17,19,1C-1F / execute
            // event 20-2F / collect
            // event 7F / ignore
            addRange(0x00, 0x17, ParserState.CSI_INTERMEDIATE, ParserAction.EXECUTE, ParserState.CSI_INTERMEDIATE)
            addRange(0x1c, 0x1f, ParserState.CSI_INTERMEDIATE, ParserAction.EXECUTE, ParserState.CSI_INTERMEDIATE)
            add(0x19, ParserState.CSI_INTERMEDIATE, ParserAction.EXECUTE, ParserState.CSI_INTERMEDIATE)
            add(0x7f, ParserState.CSI_INTERMEDIATE, ParserAction.IGNORE, ParserState.CSI_INTERMEDIATE)

            // csi intermediate -> ground
            // 40-7E / csi_dispatch
            addRange(0x40, 0x7e, ParserState.CSI_INTERMEDIATE, ParserAction.CSI_DISPATCH, ParserState.GROUND)


            // csi param -> csi ignore
            // 3A,3C-3F
            addRange(0x3c, 0x3f, ParserState.CSI_PARAM, ParserAction.IGNORE, ParserState.CSI_IGNORE)
            // event 00-17,19,1C-1F / execute
            addRange(0x00, 0x17, ParserState.CSI_IGNORE, ParserAction.EXECUTE, ParserState.CSI_IGNORE)
            addRange(0x1c, 0x1f, ParserState.CSI_IGNORE, ParserAction.EXECUTE, ParserState.CSI_IGNORE)
            add(0x19, ParserState.CSI_IGNORE, ParserAction.EXECUTE, ParserState.CSI_IGNORE)
            // event 20-3F,7F / ignore
            addRange(0x20, 0x3f, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.CSI_IGNORE)
            add(0x7f, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.CSI_IGNORE)

            // csi ignore -> ground
            addRange(0x40, 0x7e, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.GROUND)

            // csi intermediate -> csi ignore
            // 30-3F
            addRange(0x30, 0x3f, ParserState.CSI_INTERMEDIATE, ParserAction.IGNORE, ParserState.CSI_IGNORE)

            // csi entry -> csi intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.CSI_ENTRY, ParserAction.COLLECT, ParserState.CSI_INTERMEDIATE)

            // csi entry -> ground
            // 40-7E / csi_dispatch
            addRange(0x40, 0x7e, ParserState.CSI_ENTRY, ParserAction.CSI_DISPATCH, ParserState.GROUND)

            // --------------------------------------- osc --------------------------
            // escape -> osc string
            // 5D
            // entry / osc_start
            add(0x5d, ParserState.ESCAPE, ParserAction.OSC_START, ParserState.OSC_STRING)

            // event 00-17,19,1C-1F / ignore
            addRange(0x00, 0x17, ParserState.OSC_STRING, ParserAction.IGNORE, ParserState.OSC_STRING)
            addRange(0x1c, 0x1f, ParserState.OSC_STRING, ParserAction.IGNORE, ParserState.OSC_STRING)
            add(0x19, ParserState.OSC_STRING, ParserAction.IGNORE, ParserState.OSC_STRING)

            // event 20-7F / osc_put
            addRange(0x20, 0x7f, ParserState.OSC_STRING, ParserAction.OSC_PUT, ParserState.OSC_STRING)

            // exit / osc_end
            add(0x9c, ParserState.OSC_STRING, ParserAction.OSC_END, ParserState.GROUND)

            // ---------------------------- dcs -------------------------------------------
            // escape -> dcs entry 50
            // entry / clear
            add(0x50, ParserState.ESCAPE, ParserAction.CLEAR, ParserState.DCS_ENTRY)
            // event 00-17,19,1C-1F / ignore event 7F / ignore
            add(0x19, ParserState.DCS_ENTRY, ParserAction.IGNORE, ParserState.DCS_ENTRY)
            add(0x7f, ParserState.DCS_ENTRY, ParserAction.IGNORE, ParserState.DCS_ENTRY)
            addRange(0x00, 0x17, ParserState.DCS_ENTRY, ParserAction.IGNORE, ParserState.DCS_ENTRY)
            addRange(0x1c, 0x1f, ParserState.DCS_ENTRY, ParserAction.IGNORE, ParserState.DCS_ENTRY)

            // dcs entry -> dcs intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.DCS_ENTRY, ParserAction.COLLECT, ParserState.DCS_INTERMEDIATE)
            // event 00-17,19,1C-1F / ignore  event 7F / ignore
            add(0x19, ParserState.DCS_INTERMEDIATE, ParserAction.IGNORE, ParserState.DCS_INTERMEDIATE)
            add(0x7f, ParserState.DCS_INTERMEDIATE, ParserAction.IGNORE, ParserState.DCS_INTERMEDIATE)
            addRange(0x00, 0x17, ParserState.DCS_INTERMEDIATE, ParserAction.IGNORE, ParserState.DCS_INTERMEDIATE)
            addRange(0x1c, 0x1f, ParserState.DCS_INTERMEDIATE, ParserAction.IGNORE, ParserState.DCS_INTERMEDIATE)
            // event 20-2F / collect
            addRange(0x20, 0x2f, ParserState.DCS_INTERMEDIATE, ParserAction.COLLECT, ParserState.DCS_INTERMEDIATE)

            // dcs entry -> dcs ignore
            // 3A
            add(0x3a, ParserState.DCS_ENTRY, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            // event 00-17,19,1C-1F,20-7F / ignore
            addRange(0x00, 0x17, ParserState.DCS_IGNORE, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            add(0x19, ParserState.DCS_IGNORE, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            addRange(0x1c, 0x1f, ParserState.DCS_IGNORE, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            addRange(0x20, 0x7f, ParserState.DCS_IGNORE, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            // dcs ignore -> ground 9c
            add(0x9c, ParserState.DCS_IGNORE, ParserAction.IGNORE, ParserState.GROUND)

            // dcs entry -> dcs param
            // 30-39,3B / param
            // 3C-3F / collect
            add(0x3b, ParserState.DCS_ENTRY, ParserAction.PARAM, ParserState.DCS_PARAM)
            addRange(0x30, 0x39, ParserState.DCS_ENTRY, ParserAction.PARAM, ParserState.DCS_PARAM)
            addRange(0x3c, 0x3f, ParserState.DCS_ENTRY, ParserAction.COLLECT, ParserState.DCS_PARAM)

            // event 00-17,19,1C-1F / ignore
            add(0x19, ParserState.DCS_PARAM, ParserAction.IGNORE, ParserState.DCS_PARAM)
            addRange(0x00, 0x17, ParserState.DCS_PARAM, ParserAction.IGNORE, ParserState.DCS_PARAM)
            addRange(0x1c, 0x1f, ParserState.DCS_PARAM, ParserAction.IGNORE, ParserState.DCS_PARAM)

            // event 30-39,3B / param
            addRange(0x30, 0x39, ParserState.DCS_PARAM, ParserAction.PARAM, ParserState.DCS_PARAM)
            add(0x3b, ParserState.DCS_PARAM, ParserAction.PARAM, ParserState.DCS_PARAM)

            // event 7F / ignore
            add(0x7f, ParserState.DCS_PARAM, ParserAction.IGNORE, ParserState.DCS_PARAM)


            // dcs entry -> dcs pass through
            // 40-7E
            // entry / hook
            addRange(0x40, 0x7e, ParserState.DCS_ENTRY, ParserAction.DCS_HOOK, ParserState.DCS_PASS_THROUGH)
            // event 00-17,19,1C-1F,20-7E / put
            addRange(0x00, 0x17, ParserState.DCS_PASS_THROUGH, ParserAction.DCS_PUT, ParserState.DCS_PASS_THROUGH)
            addRange(0x1c, 0x1f, ParserState.DCS_PASS_THROUGH, ParserAction.DCS_PUT, ParserState.DCS_PASS_THROUGH)
            addRange(0x20, 0x7e, ParserState.DCS_PASS_THROUGH, ParserAction.DCS_PUT, ParserState.DCS_PASS_THROUGH)
            add(0x19, ParserState.DCS_PASS_THROUGH, ParserAction.DCS_PUT, ParserState.DCS_PASS_THROUGH)

            // event 7F / ignore
            add(0x7f, ParserState.DCS_PASS_THROUGH, ParserAction.IGNORE, ParserState.DCS_PASS_THROUGH)

            // exit / unhook 9c
            add(0x9c, ParserState.DCS_PASS_THROUGH, ParserAction.DCS_UNHOOK, ParserState.GROUND)

            // dcs param -> dcs ignore
            // 3A,3C-3F
            add(0x3a, ParserState.DCS_PARAM, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            addRange(0x3c, 0x3f, ParserState.DCS_PARAM, ParserAction.IGNORE, ParserState.DCS_IGNORE)

            // dcs intermediate -> dcs ignore
            // 30 - 3f
            addRange(0x30, 0x3f, ParserState.DCS_INTERMEDIATE, ParserAction.IGNORE, ParserState.DCS_IGNORE)

            // dcs intermediate -> dcs pass through
            // 40-7E
            addRange(0x40, 0x7e, ParserState.DCS_INTERMEDIATE, ParserAction.DCS_HOOK, ParserState.DCS_PASS_THROUGH)

            // dcs param -> dcs intermediate
            // 20-2F / collect
            addRange(0x20, 0x2f, ParserState.DCS_PARAM, ParserAction.COLLECT, ParserState.DCS_INTERMEDIATE)

            // dcs param -> dcs pass through
            // 40-7E
            addRange(0x40, 0x7e, ParserState.DCS_PARAM, ParserAction.DCS_HOOK, ParserState.DCS_PASS_THROUGH)

            // unicode character
            add(NON_ASCII_PRINTABLE, ParserState.GROUND, ParserAction.PRINT, ParserState.GROUND)
            add(NON_ASCII_PRINTABLE, ParserState.OSC_STRING, ParserAction.OSC_PUT, ParserState.OSC_STRING)
            add(NON_ASCII_PRINTABLE, ParserState.CSI_IGNORE, ParserAction.IGNORE, ParserState.CSI_IGNORE)
            add(NON_ASCII_PRINTABLE, ParserState.DCS_IGNORE, ParserAction.IGNORE, ParserState.DCS_IGNORE)
            add(NON_ASCII_PRINTABLE, ParserState.DCS_PASS_THROUGH, ParserAction.DCS_PUT, ParserState.DCS_PASS_THROUGH)
        }
    }

    fun onIntArray(intArray: Array<Int>) {
        intArray.forEach { onChar(it) }
    }

    fun onCharArray(charArray: Array<Char>) {
        charArray.forEach { onChar(it.code) }
    }

    fun onCharArray(charArray: CharArray) {
        charArray.forEach { onChar(it.code) }
    }

    private fun onChar(code: Int) {
        val (nextAction, nextState) = transitionTable.queryTable(code, currentState)
        if (logger.isDebugEnabled) {
            logger.debug("ON CHAR [${if (code.toChar().isISOControl()) code else code.toChar()}($code)]")
            logger.debug(
                "CURRENT: [ {}, {} ] -> NEXT: [ {}, {} ]", currentState, code, nextState, nextAction
            )
        }
        when (nextAction) {
            ParserAction.IGNORE, ParserAction.ERROR -> {}
            ParserAction.PRINT -> {
                terminal.terminalOutputProcessor.print(code)
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
//                escHandler.escDispatch(params)
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