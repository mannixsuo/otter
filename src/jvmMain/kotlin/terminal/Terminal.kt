package terminal

import androidx.compose.material.Colors
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.font.FontFamily
import org.slf4j.LoggerFactory
import parser.Parser
import shell.Shell
import terminal.service.*
import ui.SingleSelection
import java.nio.charset.Charset

class Terminal(
    private val shell: Shell,
    private val terminalConfig: TerminalConfig,
    appState: AppState,
    colors: Colors,
    fontFamily: FontFamily
) {
    private val logger = LoggerFactory.getLogger(Terminal::class.java)
    private var errorInfo: String? = null
    var focused: Boolean = false
    val state: IStateService = StateService()
    val configService: IConfigService =
        ConfigService(terminalConfig.rows, terminalConfig.columns, "title", colors, fontFamily)
    private val characterService: ICharacterService = CharacterService(colors)
    val bufferService: IBufferService = BufferService(characterService, configService)
    private val tableStopService: ITableStopService = TableStopService(terminalConfig.columns, terminalConfig.rows)
    val cursorService: CursorService = CursorService()
    private val terminalOutputProcessor: ITerminalOutputProcessorService =
        TerminalOutputProcessor(bufferService, configService, characterService, cursorService)
    private val keyboardService: IKeyboardService = KeyboardService()
    private val channelInputStreamReader = shell.getChannelInputStream()
    private val channelOutputStreamWriter = shell.getChannelOutputStreamWriter()
    private val terminalInputProcessorService: ITerminalInputProcessorService =
        TerminalInputProcessor(
            channelOutputStreamWriter,
            bufferService,
            characterService,
            cursorService,
            state,
            configService,
            tableStopService
        )

    private val parser: Parser =
        Parser(
            configService,
            appState.transitionTable,
            terminalInputProcessorService,
            terminalOutputProcessor,
            channelOutputStreamWriter
        )

    private val readBuf = ByteArray(1024)
    var close: (() -> Unit) = fun() { stop() }

    lateinit var selection: SingleSelection
    val isActive: Boolean
        get() = selection.selected === this

    fun activate() {
        selection.selected = this
    }

    fun start(): Int {
        try {
            println(Charset.defaultCharset())
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
        if (focused) {
            keyboardService.onKeyEvent(event, channelOutputStreamWriter)
            return true
        }
        return false
    }

    private fun startReadFromChannel() {
        Thread {
            var length: Int
            while (channelInputStreamReader.read(readBuf).also { length = it } != -1) {
                if (logger.isDebugEnabled) {
                    logger.debug("R: {}", String(readBuf, 0, length, Charset.defaultCharset()))
                    val debugs = StringBuilder()
                    for (i in readBuf.copyOf(length)) {
                        if (i < 32) {
                            debugs.append(i)
                        } else {
                            debugs.append(Char(i.toInt()))
                        }
                        debugs.append(" ")
                    }
                    logger.debug("B: {}", debugs.toString())

                }
                parser.onCharArray(String(readBuf, 0, length, Charset.defaultCharset()).toCharArray())
            }
        }.start()
    }

}