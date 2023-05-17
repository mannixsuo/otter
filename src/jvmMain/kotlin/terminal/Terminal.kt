package terminal

import androidx.compose.material.Colors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.KeyEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import parser.Parser
import shell.Shell
import terminal.service.*
import ui.SingleSelection
import kotlin.math.max

class Terminal(
    private val shell: Shell,
    private val terminalConfig: TerminalConfig,
    appState: AppState,
    colors: Colors
) {
    private val logger = LoggerFactory.getLogger(Terminal::class.java)
    private var errorInfo: String? = null
    var version by mutableStateOf(0)
    val state: IStateService = StateService()
    val configService: IConfigService = ConfigService(terminalConfig.rows, terminalConfig.columns, "title")
    private val characterService: ICharacterService = CharacterService(colors)
    val bufferService: IBufferService = BufferService(characterService)
    private val tableStopService: ITableStopService = TableStopService(terminalConfig.columns, terminalConfig.rows)
    val cursorService: ICursorService = CursorService()
    private val terminalOutputProcessor: ITerminalOutputProcessorService =
        TerminalOutputProcessor(bufferService, configService, characterService, cursorService)
    private val keyboardService: IKeyboardService = KeyboardService()
    private val terminalInputProcessorService: ITerminalInputProcessorService =
        TerminalInputProcessor(bufferService, characterService, cursorService, state, configService, tableStopService)
    private val channelInputStreamReader = shell.getChannelInputStreamReader()
    private val channelOutputStreamWriter = shell.getChannelOutputStreamWriter()
    private val parser: Parser =
        Parser(configService, appState.transitionTable, terminalInputProcessorService, terminalOutputProcessor)

    val readBuf = CharArray(1024)
    var close: (() -> Unit) = fun() { stop() }

    lateinit var selection: SingleSelection
    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

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
        keyboardService.onKeyEvent(event, channelOutputStreamWriter)
        version = version++ % 10
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
                version = version++ % 10
                restrictCursor()
            }
        }.start()
    }

    private fun restrictCursor() {
        cursorService.scrollY = max(bufferService.activeBuffer.lineCount() - terminalConfig.rows, 0)
    }

}