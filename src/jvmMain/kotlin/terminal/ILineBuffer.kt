package terminal

import java.util.concurrent.locks.Lock


/**
 * container of lines reads from a terminal
 */
interface ILineBuffer {

    val lock: Lock

    /**
     * get line at index of the buffer
     */
    fun getLine(index: Int): ILine?

    /**
     * get lines that index in range
     */
    fun getLines(range: IntRange): List<ILine>

    fun getAllLines(): List<ILine>

    /**
     * append line to the buffer
     */
    fun appendLine(line: ILine)

    /**
     * insert line at index
     */
    fun insertLine(index: Int, line: ILine)

    /**
     * delete line at index
     *
     * line behind the index will move up
     */
    fun deleteLine(index: Int)


    /**
     * delete lines in range
     *
     * line behind the index will move up
     */
    fun deleteLines(range: IntRange)


    fun lineCount(): Int

}
