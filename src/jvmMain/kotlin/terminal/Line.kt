package terminal

import androidx.compose.material.Colors
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import emptyCell

/**
 * A line represent one line in window
 * it contains some characters
 *
 * may be empty
 */
interface ILine {
    /**
     * get cell at specific index of the line
     * may be null if index is out bound
     */
    fun getCell(index: Int): ICell

    /**
     * get all cells in this line
     */
    fun getCells(): Array<ICell>

    /**
     * append cell at last
     */
    fun appendCell(cell: ICell)

    /**
     * insert cell at specific index
     *
     * cells behind index will move back
     */
    fun insertCell(index: Int, cell: ICell)

    /**
     * insert cells at specific index
     *
     * cells behind index will move back
     */
    fun insertCells(index: Int, cells: Array<ICell>)

    /**
     * replace a cell
     */
    fun replaceCell(index: Int, replacement: ICell)

    /**
     * replace cell by range
     */
    fun replaceCells(range: IntRange, replacement: ICell)

    /**
     * delete cell at index
     *
     * cells behind the index will move forward on cell
     */
    fun deleteCell(index: Int): ICell?

    /**
     * delete cells by range
     *
     * cells behind the range end will move forward
     */
    fun deleteCells(range: IntRange)

    /**
     * max length of the line
     */
    fun maxLength(): Int

    /**
     * length of the line
     */
    fun length(): Int

    fun toAnnotatedString(cursorOnThisLine: Boolean, cursorX: Int, colors: Colors): AnnotatedString
}

class Line(private val maxLength: Int) : ILine {

    // 0 no elements , 1 has a elements eg.
    private var _length = 0

    private val _cells = Array<ICell>(maxLength) { emptyCell }

    override fun getCell(index: Int): ICell {
        return if (index < 0 || index > _length) {
            emptyCell
        } else {
            _cells[index]
        }
    }

    override fun getCells(): Array<ICell> {
        return _cells
    }

    override fun insertCell(index: Int, cell: ICell) {
        if (index in 0.._length) {
            for (i in _length downTo index + 1) {
                _cells[i] = _cells[i - 1]
            }
            _cells[index] = cell
        }
    }

    override fun insertCells(index: Int, cells: Array<ICell>) {
        if (index in 0.._length) {
            for (i in _length downTo index + cells.size) {
                _cells[i] = cells[i - 1]
            }
            for (i in index until cells.size) {
                _cells[i] = cells[i]
            }
        }
    }

    override fun appendCell(cell: ICell) {
        if (_length >= maxLength) {
            return
        }
        _cells[_length++] = cell
    }

    override fun replaceCell(index: Int, replacement: ICell) {
        _cells[index] = replacement
        _length = Math.max(index + 1, _length)
    }

    override fun replaceCells(range: IntRange, replacement: ICell) {
        val start = 0.coerceAtLeast(range.first)
        val end = _length.coerceAtMost(range.last)
        for (index in start..end) {
            _cells[index] = replacement
        }
    }

    override fun deleteCell(index: Int): ICell? {
        if (index !in 0.._length) {
            return null
        }
        val deleted = _cells[index]
        for (i in index until _length) {
            _cells[i] = _cells[i + 1]
        }
        _length -= 1
        return deleted
    }

    override fun deleteCells(range: IntRange) {
        val start = 0.coerceAtLeast(range.first)
        val end = _length.coerceAtMost(range.last)
        for (i in start until end) {
            _cells[i] = _cells[end + 1]
            _length--
        }
    }

    override fun maxLength(): Int {
        return maxLength
    }

    override fun length(): Int {
        return _length
    }

    override fun toAnnotatedString(cursorOnThisLine: Boolean, cursorX: Int, colors: Colors): AnnotatedString {
        return buildAnnotatedString {
            for (index in 0 until _length.coerceAtLeast(cursorX)) {
                val cursorInThisPosition: Boolean = cursorOnThisLine && cursorX == index

                with(getCell(index)) {
                    var text = char
                    if (char.code == 0) {
                        text = if (cursorInThisPosition) '_' else ' '
                    }
                    var spanStyle = SpanStyle(
                        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
                    )

                    if (cursorInThisPosition) {
                        spanStyle = SpanStyle(
                            background = colors.primary,
                            color = colors.background,
                            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
                        )
                    }

                    withStyle(
                        style = spanStyle
                    ) {
                        append(text)
                    }
                }
            }
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (index in 0 until _length) {
            builder.append(getCell(index).char)
        }
        return String(builder)
    }
}