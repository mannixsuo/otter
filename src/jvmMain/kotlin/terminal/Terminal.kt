package terminal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import org.slf4j.LoggerFactory
import parser.Parser
import shell.Shell
import terminal.service.BufferService
import terminal.service.CharacterService
import terminal.service.IBufferService
import terminal.service.TableStopService
import ui.SingleSelection
import ui.scroll.ScrollState

@OptIn(ExperimentalComposeUiApi::class)
class Terminal(val shell: Shell, val terminalConfig: TerminalConfig) {

    var errorInfo: String? = null
    val keyboard = Keyboard()
    private val logger = LoggerFactory.getLogger(Terminal::class.java)
    val bufferService: IBufferService = BufferService()
    val terminalInputProcessor = TerminalInputProcessor(this)
    val terminalOutputProcessor = TerminalOutputProcessor(this)
    var title: String by mutableStateOf("Terminal")
    val state = TerminalState()

    val tableStopService = TableStopService(terminalConfig.columns, terminalConfig.rows)

    var close: (() -> Unit) = fun() { stop() }
    val scrollState by mutableStateOf(ScrollState())

    lateinit var selection: SingleSelection
    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    /**
     * current cursor position x
     */
    var cursorX by mutableStateOf(0)

    var scrollX by mutableStateOf(0)

    /**
     * current cursor position y
     */
    var cursorY by mutableStateOf(0)

    var scrollY by mutableStateOf(0)


    private val channelInputStreamReader = shell.getChannelInputStreamReader()
    private val channelOutputStreamWriter = shell.getChannelOutputStreamWriter()
    private val parser: Parser = Parser(this)

    val characterService = CharacterService()


    fun start(): Int {
        try {
            startReadFromChannel()
        } catch (exception: Exception) {
            logger.error("error start terminal", exception)
            errorInfo = exception.message
            return -1
        }
        return 0
    }

    fun stop() {
        println("terminal stop")
        shell.close()
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.type == KeyEventType.KeyDown) {
            when (event.key) {
                Key.ShiftLeft, Key.ShiftRight, Key.CtrlLeft, Key.CtrlRight, Key.AltLeft, Key.AltRight -> return true
            }
            val toInt = event.utf16CodePoint
            val keyChar = keyboard.getKeyChar(event.key)
            if (keyChar == null) {
                channelOutputStreamWriter.write(toInt)
            } else {
                channelOutputStreamWriter.write(keyChar)
            }
            channelOutputStreamWriter.flush()
            return true
        }
        return true
    }

    private fun startReadFromChannel() {
        Thread {
            val buf = CharArray(1024)
            var length: Int
            while (channelInputStreamReader.read(buf).also { length = it } != -1) {
                if (logger.isDebugEnabled) {
                    logger.debug("read from channel")
                    logger.debug(String(buf, 0, length))
                }
                parser.onCharArray(buf.copyOfRange(0, length))
                restrictCursor()
            }
        }.start()
    }

    private fun restrictCursor() {
        scrollState.y = Math.max(bufferService.getActiveBuffer().lineCount() - terminalConfig.rows, 0)
    }

}