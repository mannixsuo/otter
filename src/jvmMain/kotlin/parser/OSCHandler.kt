package parser

import org.slf4j.LoggerFactory
import terminal.service.IConfigService
import java.io.OutputStreamWriter

class OSCHandler(private val configService: IConfigService, private val channelOutputStreamWriter: OutputStreamWriter) {

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

    /**
     * Set Text Parameters.
     * OSC P s ; P t ST
     *
     * OSC P s ; P t BEL
     */
    private fun handleOscCommand(buffer: StringBuffer) {
        val params = buffer.toString().split(";")
        if (params[0] == "0") {
            changeIconNameAndWindowTitle(buffer)
        }
        if (params[0] == "10") {
            setOrReportFgColor(params[1])
        }

    }


    /**
     * OSC 10 ; <xcolor name>|<?> ST - set or query default foreground color
     *
     * @vt: #Y  OSC   10    "Set or query default foreground color"   "OSC 10 ; Pt BEL"  "Set or query default foreground color."
     * To set the color, the following color specification formats are supported:
     * - `rgb:<red>/<green>/<blue>` for  `<red>, <green>, <blue>` in `h | hh | hhh | hhhh`, where
     *   `h` is a single hexadecimal digit (case insignificant). The different widths scale
     *   from 4 bit (`h`) to 16 bit (`hhhh`) and get converted to 8 bit (`hh`).
     * - `#RGB` - 4 bits per channel, expanded to `#R0G0B0`
     * - `#RRGGBB` - 8 bits per channel
     * - `#RRRGGGBBB` - 12 bits per channel, truncated to `#RRGGBB`
     * - `#RRRRGGGGBBBB` - 16 bits per channel, truncated to `#RRGGBB`
     *
     * **Note:** X11 named colors are currently unsupported.
     *
     * If `Pt` contains `?` instead of a color specification, the terminal
     * returns a sequence with the current default foreground color
     * (use that sequence to restore the color after changes).
     *
     * **Note:** Other than xterm, xterm.js does not support OSC 12 - 19.
     * Therefore, stacking multiple `Pt` separated by `;` only works for the first two entries.
     */
    private fun setOrReportFgColor(s: String) {
        if (s == "?") {
            val toArgb = toRgbString(
                configService.colors.primary.red.toInt(),
                configService.colors.primary.green.toInt(),
                configService.colors.primary.blue.toInt()
            )
            val fgs = "${Char(27)}]10;${toArgb}${Char(27)}\\"
            logger.info("TO RGB {}", fgs)

            channelOutputStreamWriter.write(fgs)
        }
    }

    private fun changeIconNameAndWindowTitle(buffer: StringBuffer) {
        configService.title = buffer.substring(buffer.indexOf(';') + 1)
    }

    private fun toRgbString(r: Int, g: Int, b: Int): String {
        return "rgb:${r.toString(16).padStart(2, '0')}/${g.toString(16).padStart(2, '0')}/${
            b.toString(16).padStart(2, '0')
        }"
    }
}