package terminal.service

import java.util.*


class TableStopService(private val maxColumns: Int, maxRows: Int) {
    val verticalTableStops: Array<Int> = Array(maxRows) { -1 }
    private val horizontalTableStops: Array<Int> = Array(maxColumns) { -1 }


    fun getNextHorizontalTableStop(current: Int): Int? {
        if (horizontalTableStops.isEmpty()) {
            return null
        }
        for (index in current until horizontalTableStops.size) {
            if (horizontalTableStops[index] != -1) {
                return index
            }
        }
        return null
    }
}