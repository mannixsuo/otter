package parser

import org.slf4j.LoggerFactory
import terminal.CSIProcessor
import java.util.*

// Type  Size(bits)
// Byte    8
// Short   16
// Int     32
// Long    64
//
// | finalChar | intermediate | prefix
// 8bit + 8bit + 8bit
// CSI Ps * x ------- | x | * | 0 |
// CSI Ps SP t ------ | t | SP| 0 |
// CSI ? Ps $ p ----- | p | $ | ? |
const val prefixShift = 8

data class CsiCommand(val finalChar: Char, val prefix: Char?, val intermediate: Char?) {

    fun key(): Int {
        return generateKey(finalChar, prefix, intermediate)
    }
}

fun generateKey(finalChar: Char, prefix: Char?, intermediate: Char?): Int {
    return (if (prefix == null) 0 else prefix.code shl 14) or (if (intermediate == null) 0 else intermediate.code shl 8) or (finalChar.code)
}


fun generateKey(finalCharCode: Int, prefix: Char?, intermediate: Char?): Int {
    return (if (prefix == null) 0 else prefix.code shl 14) or (if (intermediate == null) 0 else intermediate.code shl 8) or (finalCharCode)
}

class CsiHandler(private val csiProcessor: CSIProcessor) {
    private val logger = LoggerFactory.getLogger(CsiHandler::class.java)

    fun csiDispatch(collect: Stack<Char>, params: Params, finalCharCode: Int) {

        var prefix: Char? = null
        var intermediate: Char? = null

        if (collect.size == 2) {
            intermediate = collect.pop()
            prefix = collect.pop()
        }
        if (collect.size == 1) {
            prefix = collect.pop()
        }

        val key = generateKey(finalCharCode, prefix, intermediate)
        if (logger.isDebugEnabled) {
            logger.debug(
                "CSI {} {} {} {}",
                prefix ?: ' ',
                intermediate ?: ' ',
                params.toIntArray(),
                Char(finalCharCode)
            )
        }
        if (commandExecutorMap.containsKey(key)) {
            commandExecutorMap[key]?.invoke(params.toIntArray())
        } else {
            logger.warn("NO CSI COMMAND HANDLER FOUND FOR CSI ${prefix ?: ""} ${params.toParamString()}  ${intermediate ?: ""} ${finalCharCode.toChar()}")
        }

    }

    private val commandExecutorMap = TreeMap<Int, CsiHandlerFun>()

    init {
        with(commandExecutorMap) {
            with(csiProcessor) {
                put(CsiCommand('@', null, null).key()) { params -> insertChars(params) }
                put(CsiCommand('@', null, ' ').key()) { params -> shiftLeft(params) }
                put(CsiCommand('A', null, null).key()) { params -> cursorUp(params) }
                put(CsiCommand('A', null, ' ').key()) { params -> cursorRight(params) }
                put(CsiCommand('B', null, null).key()) { params -> cursorDown(params) }
                put(CsiCommand('C', null, null).key()) { params -> cursorForward(params) }
                put(CsiCommand('D', null, null).key()) { params -> cursorBackward(params) }
                put(CsiCommand('E', null, null).key()) { params -> cursorNextLine(params) }
                put(CsiCommand('F', null, null).key()) { params -> cursorPrecedingLine(params) }
                put(CsiCommand('G', null, null).key()) { params -> cursorCharacterAbsolute(params) }
                put(CsiCommand('H', null, null).key()) { params -> cursorPosition(params) }
                put(CsiCommand('I', null, null).key()) { params -> cursorForwardTabulation(params) }
                put(CsiCommand('J', null, null).key()) { params -> eraseInDisplay(params) }
                put(CsiCommand('J', '?', null).key()) { params -> eraseInDisplaySelective(params) }
                put(CsiCommand('K', null, null).key()) { params -> eraseInLine(params) }
                put(CsiCommand('K', '?', null).key()) { params -> eraseInLineSelective(params) }
                put(CsiCommand('L', null, null).key()) { params -> insertLines(params) }
                put(CsiCommand('M', null, null).key()) { params -> deleteLines(params) }
                put(CsiCommand('P', null, null).key()) { params -> deleteCharacters(params) }
                put(CsiCommand('h', '?', null).key()) { params -> decPrivateModeSet(params) }
                put(CsiCommand('l', '?', null).key()) { params -> decPrivateModeReSet(params) }
                put(CsiCommand('m', null, null).key()) { params -> characterAttributes(params) }
            }

        }
    }
}


typealias CsiHandlerFun = (params: Array<Int>) -> Unit
