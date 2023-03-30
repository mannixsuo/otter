package parser

import org.slf4j.LoggerFactory
import terminal.Terminal

class OSCHandler(private val terminal: Terminal) {

    private val logger = LoggerFactory.getLogger(OSCHandler::class.java)

    private val buffer = StringBuffer()

    fun reset() {
//        TODO("Not yet implemented")
    }

    fun put(code: Int) {
        buffer.append(code.toChar())
    }

    fun finish() {
        handleOscCommand(buffer)
        buffer.delete(0, buffer.length)
    }

    private fun handleOscCommand(buffer: StringBuffer) {
        if (logger.isDebugEnabled) {
            logger.debug("osc buffer {}", buffer)
        }
        when (buffer[0]) {
            '0' -> changeIconNameAndWindowTitle(buffer)
        }

    }

    private fun changeIconNameAndWindowTitle(buffer: StringBuffer) {
        terminal.title = buffer.substring(buffer.indexOf(';') + 1)
    }
}