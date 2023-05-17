package terminal.service

/**
 * print characters to current active buffer
 */
interface ITerminalOutputProcessorService {
    fun print(code: Int)
}