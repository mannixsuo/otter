package parser

import org.slf4j.LoggerFactory
import terminal.Line
import terminal.Terminal

class SingleCharacterFunProcessor(private val terminal: Terminal) {
    private val logger = LoggerFactory.getLogger(SingleCharacterFunProcessor::class.java)
    private val commandExecutorMap = HashMap<Int, SingleCharacterFun>()

    fun handleCode(code: Int) {
        with(commandExecutorMap) {
            if (containsKey(code)) {
                get(code)!!.invoke()
            } else {
                logger.info("NO C0C1CONTROLFUNCTIONEXECUTOR FOUND FOR $code")
            }
        }
    }

    init {
        // VT100
        commandExecutorMap[0] = { doNothing() }
        commandExecutorMap[5] = { transmitAnswerBackMessage() }
        commandExecutorMap[7] = { bell() }
        commandExecutorMap[8] = { backSpace() }
        commandExecutorMap[9] = { ht() }
        commandExecutorMap[10] = { newLine() }
        commandExecutorMap[11] = { newLine() }
        commandExecutorMap[12] = { newLine() }
        commandExecutorMap[13] = { carriageReturn() }
        commandExecutorMap[14] = { invokeG1CharacterSet() }
        commandExecutorMap[15] = { selectG0CharacterSet() }

    }

    /**
     * Select G0 character set, as selected by ESC ( sequence.
     */
    private fun selectG0CharacterSet() {
        TODO("Not yet implemented")
    }

    /**
     * Invoke G1 character set, as designated by SCS control sequence.
     */
    private fun invokeG1CharacterSet() {
        TODO("Not yet implemented")
    }

    /**
     * Move the cursor to the next tab stop, or to the right margin if no further tab stops are present on the line.
     */
    private fun ht() {
        TODO("Not yet implemented")
    }

    /**
     * Move the cursor to the left one character position, unless it is at the left margin, in which case no action occurs.
     */
    private fun backSpace() {
        terminal.cursorX -= 1
        if (terminal.cursorX < 0) {
            terminal.cursorX = 0
        }
    }

    /**
     * Sound bell tone from keyboard.
     */
    private fun bell() {

    }


    /**
     * Transmit answerback message.
     */
    private fun transmitAnswerBackMessage() {
        TODO()
    }

    private fun doNothing() {}

    /**
     * Move cursor to the left margin on the current line.
     *
     */
    private fun carriageReturn() {
        terminal.cursorX = 0
        terminal.scrollX = 0
    }

    /**
     * This code causes a line feed or a new line operation. (See new line mode).
     * make a new line at current line
     */
    private fun newLine() {
        terminal.cursorY += 1
        if (terminal.cursorY >= terminal.terminalConfig.rows) {
            terminal.scrollY++
            terminal.cursorY = terminal.terminalConfig.rows - 1
        }
        terminal.bufferService.getActiveBuffer()
            .insertLine(terminal.scrollY + terminal.cursorY, Line(terminal.terminalConfig.columns))
    }
}

typealias SingleCharacterFun = () -> Unit