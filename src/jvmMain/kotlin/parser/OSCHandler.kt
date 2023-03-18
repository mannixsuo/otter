package parser

class OSCHandler {

    private val buffer = StringBuffer()

    fun reset() {
//        TODO("Not yet implemented")
    }

    fun put(code: Int) {
        buffer.append(code.toChar())
    }

    fun finish() {
        handleOscCommand(buffer)
    }

    private fun handleOscCommand(buffer: StringBuffer) {
        println(buffer)
    }
}