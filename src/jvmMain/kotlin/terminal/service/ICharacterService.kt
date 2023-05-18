package terminal.service

import terminal.ICell


/**
 * character service of the terminal control character color and style
 */
interface ICharacterService {
    /**
     * change current style to normal style
     */
    fun normal()

    /**
     * change current character style to bold
     */
    fun bold()

    /**
     * change current character style to italic
     */
    fun italic()

    /**
     * create an empty cell
     */
    fun createEmptyCell(): ICell

    fun buildCell(code: Char): ICell

    fun buildEmptyCells(count: Int): Array<ICell>

    fun fgYellow()

}