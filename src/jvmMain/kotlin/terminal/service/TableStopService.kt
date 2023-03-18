package terminal.service

import java.util.*


class TableStopService(private val maxColumns: Int, maxRows: Int) {
    val verticalTableStops: Array<Int> = Array(maxRows) { -1 }
    private val horizontalTableStops: Array<Int> = Array(maxColumns) { -1 }


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
//        if (horizontalTableStops.isEmpty()) {
//            return null
//        }
//        for (index in current until horizontalTableStops.size) {
//            if (horizontalTableStops[index] != -1) {
//                return index
//            }
//        }
//        return null
    }
}