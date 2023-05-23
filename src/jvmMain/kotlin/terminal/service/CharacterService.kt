package terminal.service

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import terminal.Cell
import terminal.ICell

class CharacterService(private val colors: Colors) : ICharacterService {

    private var nextCharFgColor: Color = colors.primary
    private var nextCharBgColor: Color? = null
    private var nextCharBold = false
    private var nextCharItalic = false
    private var nextCharUnderLine = false

    override fun normal() {
        nextCharFgColor = colors.primary
        nextCharBgColor = null
        nextCharBold = false
        nextCharItalic = false
        nextCharUnderLine = false
    }

    override fun bold() {
        nextCharBold = true
    }

    override fun italic() {
        nextCharItalic = true
    }

    fun italicized() {
        nextCharItalic = true
    }

    override fun fgYellow() {
        nextCharFgColor = Color.Yellow
    }

    override fun fgGreen() {
        nextCharFgColor = Color.Green
    }

    override fun fgBlue() {
        nextCharFgColor = Color.Blue
    }

    override fun fgBlack() {
        nextCharFgColor = Color.Black
    }

    override fun fgRed() {
        nextCharFgColor = Color.Red
    }

    override fun fgMagenta() {
        nextCharFgColor = Color.Magenta
    }

    override fun fgWhite() {
        nextCharFgColor = Color.White
    }

    override fun fgCyan() {
        nextCharFgColor = Color.Cyan
    }

    override fun fgDefault() {
        nextCharFgColor = colors.primary
    }

    override fun bgDefault() {
        nextCharBgColor = null
    }

    override fun underLine() {
        nextCharUnderLine = true
    }

    override fun bgBlack() {
        nextCharBgColor = Color.Black
    }

    override fun bgRed() {
        nextCharBgColor = Color.Red
    }

    override fun bgGreen() {
        nextCharBgColor = Color.Green
    }

    override fun bgYellow() {
        nextCharBgColor = Color.Yellow
    }

    override fun bgBlue() {
        nextCharBgColor = Color.Blue
    }

    override fun bgMagenta() {
        nextCharBgColor = Color.Magenta
    }

    override fun bgCyan() {
        nextCharBgColor = Color.Cyan
    }

    override fun bgWhite() {
        nextCharBgColor = Color.White
    }

    override fun buildCell(char: Char): ICell {
        return Cell(
            char,
            nextCharBgColor,
            nextCharFgColor,
            nextCharBold,
            nextCharItalic
        )
    }

    override fun createEmptyCell(): ICell {
        return buildCell(Char.MIN_VALUE)
    }


    override fun buildEmptyCells(count: Int): Array<ICell> {
        return Array(count) { buildCell(Char.MIN_VALUE) }
    }
}