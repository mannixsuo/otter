package terminal.service

class TableStopService(private val maxColumns: Int, maxRows: Int) {
    
    fun getNextHorizontalTableStop(current: Int): Int {
        //vt100
        //T_______T_______T
        //0123456789012345678901234567890
        val nextTabStop = (current / 8 + 1) * 8
        return if (nextTabStop >= maxColumns) {
            maxColumns - 1
        } else {
            nextTabStop
        }
    }
}