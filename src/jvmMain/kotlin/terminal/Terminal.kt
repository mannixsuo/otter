package terminal

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import org.slf4j.LoggerFactory
import parser.Parser
import shell.Shell
import terminal.service.BufferService
import terminal.service.IBufferService
import ui.AppTheme
import ui.SingleSelection

@OptIn(ExperimentalComposeUiApi::class)
class Terminal(val shell: Shell, val terminalConfig: TerminalConfig) {
    val keyboard = Keyboard()
    private val logger = LoggerFactory.getLogger(Terminal::class.java)
    val bufferService: IBufferService = BufferService()
    val terminalInputProcessor = TerminalInputProcessor(this)
    val terminalOutputProcessor = TerminalOutputProcessor(this)
    val title: String = "Terminal Title"
    val state = TerminalState()

    var close: (() -> Unit) = fun() { stop() }
    var onLineChange: (() -> Unit)? = null
    var viewInitialized: (() -> Boolean) = { false }

    lateinit var selection: SingleSelection
    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
        onLineChange?.invoke()
    }

    /**
     * current cursor position x
     */
    var cursorX = 0

    var scrollX = 0

    /**
     * current cursor position y
     */
    var cursorY = 0

    var scrollY = 0


    private val channelInputStreamReader = shell.getChannelInputStreamReader()
    private val channelOutputStreamWriter = shell.getChannelOutputStreamWriter()
    private val parser: Parser = Parser(this)


    var nextCharFgColor = AppTheme.colors.material.primary
    var nextCharBgColor = AppTheme.colors.material.background
    var nextCharBold = false
    var nextCharItalic = false


    fun start() {
        startReadFromChannel()
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
                parser.onCharArray(buf.copyOfRange(0, length))
                print(String(buf, 0, length))
                refreshUI()
            }
        }.start()
    }

    fun refreshUI() {
        if (viewInitialized()) {
            if (isActive) {
                onLineChange?.invoke()
            }
        }
    }

}