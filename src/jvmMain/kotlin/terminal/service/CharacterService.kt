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

    override fun normal() {
        nextCharFgColor = colors.primary
        nextCharBgColor = null
        nextCharBold = false
        nextCharItalic = false
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

    fun fgDefault() {
        nextCharFgColor = colors.primary
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