package terminal

import terminal.service.*
import kotlin.math.max

class CSIProcessor(
    private val bufferService: IBufferService,
    private val characterService: ICharacterService,
    private val cursorService: ICursorService,
    private val stateService: IStateService,
    private val configService: IConfigService
) {

    /**
     * CSI Ps @
     * Insert Ps (Blank) Character(s) (default = 1) (ICH).
     */
    fun insertChars(params: Array<Int>) {
        val activeBuffer = bufferService.activeBuffer
        with(activeBuffer) {
            val cells = characterService.buildEmptyCells(params.elementAtOrElse(0) { 1 })
            getLine(cursorService.getAbsoluteRowNumber())?.insertCells(
                cursorService.getAbsoluteColumnNumber(), cells
            )
        }
    }

    /**
     * CSI Ps SP @
     * Shift left Ps columns(s) (default = 1) (SL), ECMA-48.
     */
    fun shiftLeft(params: Array<Int>) {
        val activeBuffer = bufferService.activeBuffer
        val shiftCount = params.elementAtOrElse(0) { 1 }
        with(activeBuffer) {
            getLine(cursorService.getAbsoluteRowNumber())?.getCells()?.let {
                for (index in 0 until it.size - shiftCount) {
                    it[index] = it[index + 1]
                }
            }
        }
    }

    /**
     * CSI Ps A
     * Cursor Up Ps Times (default = 1) (CUU).
     */
    fun cursorUp(params: Array<Int>) {
        cursorService.up(params.elementAtOrElse(0) { 1 })
        if (cursorService.cursorY < 0) {
            cursorService.cursorY = 0
        }
    }

    /**
     * CSI Ps SP A
     * Shift right Ps columns(s) (default = 1) (SR), ECMA-48.
     */
    fun cursorRight(params: Array<Int>) {
        val activeBuffer = bufferService.activeBuffer
        val shiftCount = params.elementAtOrElse(0) { 1 }

        with(activeBuffer) {
            getLine(cursorService.getAbsoluteRowNumber())?.getCells()?.let {
                for (index in it.size - 1 downTo shiftCount) {
                    it[index] = it[index - shiftCount]
                }
                for (index in 0 until shiftCount) {
                    it[index] = characterService.createEmptyCell()
                }
            }
        }
    }

    /**
     * CSI Ps B
     * Cursor Down Ps Times (default = 1) (CUD).
     */
    fun cursorDown(params: Array<Int>) {
        cursorService.down(params.elementAtOrElse(0) { 1 })
    }

    /**
     * CSI Ps C
     * Cursor Forward Ps Times (default = 1) (CUF).
     * The CUF sequence moves the active position to the right. The distance moved is determined by the parameter.
     * A parameter value of zero or one moves the active position one position to the right.
     * A parameter value of n moves the active position n positions to the right.
     * If an attempt is made to move the cursor to the right of the right margin, the cursor stops at the right margin.
     */
    fun cursorForward(params: Array<Int>) {
        val step = max(1, params[0])
        cursorService.forward(step)
    }

    /**
     * CSI Ps D
     * Cursor Backward Ps Times (default = 1) (CUB).
     */
    fun cursorBackward(params: Array<Int>) {
        val step = max(1, params[0])
        cursorService.back(step)
    }

    /**
     * CSI Ps E
     * Cursor Next Line Ps Times (default = 1) (CNL).
     */
    fun cursorNextLine(params: Array<Int>) {
        val step = max(1, params[0])
        cursorService.down(step)
    }

    /**
     * CSI Ps F
     * Cursor Preceding Line Ps Times (default = 1) (CPL).
     */
    fun cursorPrecedingLine(params: Array<Int>) {
        val step = max(1, params[0])
        cursorService.up(step)
    }

    /**
     * CSI Ps G
     * Cursor Character Absolute  [column] (default = [row,1]) (CHA).
     * Moves cursor to the Ps-th column of the active line. The default value of Ps is 1.
     */
    fun cursorCharacterAbsolute(params: Array<Int>) {
        val step = max(1, params[0])
        cursorService.cursorX = step - 1
    }

    /**
     * CSI Ps ; Ps H
     * Cursor Position [row;column] (default = [1,1]) (CUP).
     */
    fun cursorPosition(params: Array<Int>) {
        cursorService.cursorY = max(1, params.elementAtOrElse(0) { 0 })
        cursorService.cursorX = max(1, params.elementAtOrElse(1) { 0 })
    }

    /**
     * CSI Ps I
     * Cursor Forward Tabulation Ps tab stops (default = 1) (CHT).
     */
    fun cursorForwardTabulation(params: Array<Int>) {
        TODO()
    }

    /**
     * CSI Ps J  Erase in Display (ED), VT100.
     * Ps = 0  ⇒  Erase Below (default).
     * Ps = 1  ⇒  Erase Above.
     * Ps = 2  ⇒  Erase All.
     * Ps = 3  ⇒  Erase Saved Lines, xterm.
     */
    fun eraseInDisplay(params: Array<Int>) {
        val activeBuffer = bufferService.activeBuffer
        with(activeBuffer) {
            when (params.elementAtOrElse(0) { 0 }) {
                0 -> {
                    this.deleteLines(
                        IntRange(
                            cursorService.getAbsoluteRowNumber() + 1,
                            cursorService.scrollY + (configService.maxRows - cursorService.cursorY)
                        )
                    )
                }

                1 -> {
                    this.deleteLines(
                        IntRange(
                            cursorService.scrollY,
                            cursorService.getAbsoluteRowNumber() - 1
                        )
                    )
                }

                2, 3 -> {
                    this.deleteLines(IntRange(0, this.lineCount()))
                }
            }
        }
    }

    /**
     * CSI ? Ps J
     * Erase in Display (DECSED), VT220.
     * Ps = 0  ⇒  Selective Erase Below (default).
     * Ps = 1  ⇒  Selective Erase Above.
     * Ps = 2  ⇒  Selective Erase All.
     * Ps = 3  ⇒  Selective Erase Saved Lines, xterm.
     */
    fun eraseInDisplaySelective(params: Array<Int>) {
        eraseInDisplay(params)
    }

    /**
     * CSI Ps K  Erase in Line (EL), VT100.
     *  Ps = 0  ⇒  Erase to Right (default).
     *  Ps = 1  ⇒  Erase to Left.
     *  Ps = 2  ⇒  Erase All.
     */
    fun eraseInLine(params: Array<Int>) {
        val activeBuffer = bufferService.activeBuffer
        with(activeBuffer) {
            when (params.elementAtOrElse(0) { 0 }) {
                0 -> {
                    getLine(cursorService.getAbsoluteRowNumber())?.deleteToRight(cursorService.getAbsoluteColumnNumber())
                }

                1 -> {
                    getLine(cursorService.getAbsoluteRowNumber())?.deleteCells(
                        IntRange(
                            0, cursorService.getAbsoluteRowNumber() + 1
                        )
                    )
                }

                2 -> {
                    getLine(cursorService.getAbsoluteRowNumber())?.let {
                        it.deleteCells(IntRange(0, it.length()))
                    }
                }

                else -> {}
            }

        }
    }

    /**
     * CSI ? Ps K
     * Erase in Line (DECSEL), VT220.
     * Ps = 0  ⇒  Selective Erase to Right (default).
     * Ps = 1  ⇒  Selective Erase to Left.
     * Ps = 2  ⇒  Selective Erase All.
     */
    fun eraseInLineSelective(params: Array<Int>) {
        eraseInLine(params)
    }

    /**
     * CSI Ps L
     * Insert Ps Line(s) (default = 1) (IL).
     */
    fun insertLines(params: Array<Int>) {
        val activeBuffer = bufferService.activeBuffer
        val param = params.elementAtOrElse(0) { 1 }
        with(activeBuffer) {
            TODO()
//            this.insertLine()
        }
    }

    /**
     * CSI Ps M
     * Delete Ps Line(s) (default = 1) (DL).
     */
    fun deleteLines(params: Array<Int>) {
        TODO("Not yet implemented")
    }

    /**
     * CSI Ps P
     * Delete Ps Character(s) (default = 1) (DCH).
     */
    fun deleteCharacters(params: Array<Int>) {
        TODO("Not yet implemented")
    }

    /**
     * CSI ? Pm h
     * DEC Private Mode Set (DECSET).
     */
    fun decPrivateModeSet(params: Array<Int>) {
        for (param in params) {
            when (param) {
                1 -> stateService.applicationCursorKeys()
                25 -> stateService.showCursor()

            }
        }
    }

    /**
     * CSI ? Pm l
     *           DEC Private Mode Reset (DECRST).
     */
    fun decPrivateModeReSet(params: Array<Int>) {
        for (param in params) {
            when (param) {
                1 -> stateService.normalCursorKeys()
                25 -> stateService.hideCursor()

            }
        }
    }

    /**
     * CSI Pm m  Character Attributes (SGR).
     */
    fun characterAttributes(params: Array<Int>) {
        for (param in params) {
            when (param) {
                0 -> characterService.normal()
                1 -> characterService.bold()
                3 -> characterService.italic()
                33, 93 -> characterService.fgYellow()
            }
        }
    }

}