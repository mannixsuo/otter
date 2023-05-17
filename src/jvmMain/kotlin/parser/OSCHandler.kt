package parser

import org.slf4j.LoggerFactory
import terminal.service.IConfigService

class OSCHandler(private val configService: IConfigService) {

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
        when (buffer[0]) {
            '0' -> changeIconNameAndWindowTitle(buffer)
        }

    }

    private fun changeIconNameAndWindowTitle(buffer: StringBuffer) {
        configService.title = buffer.substring(buffer.indexOf(';') + 1)
    }
}