package terminal.service

/**
 * service for calculate table stop
 */
interface ITableStopService {

    /**
     * calculate next table stop
     */
    fun getNextHorizontalTableStop(current: Int): Int
}