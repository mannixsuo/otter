package terminal

import androidx.compose.ui.graphics.Color
import ui.AppTheme

/**
 * 字符最小单元
 */
interface ICell {
    val char: Char
    val bg: Color
    val fg: Color
    val bold: Boolean
    val italic: Boolean
}

class Cell(
    override val char: Char,
    override val bg: Color,
    override val fg: Color,
    override val bold: Boolean,
    override val italic: Boolean
) : ICell {
    override fun toString(): String {
        return char.toString()
    }
}

