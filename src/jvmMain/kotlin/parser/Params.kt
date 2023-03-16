package parser

class Params {

    private var params: Array<Int> = Array(10) { 0 }
    private var paramCount: Int = 0

    fun reset() {
        this.params = Array(10) { 0 }
        this.paramCount = 0
    }

    fun put(code: Int) {
        if (code == 0x3b) {
            paramCount++
        } else {
            params[paramCount] = (params[paramCount] * 10) + (code - 0x30)
        }
    }

    fun get(index: Int): Int {
        if (index > paramCount) {
            return 0
        }
        return params[index]
    }

    fun toIntArray(): Array<Int> {
        return params.sliceArray(IntRange(0, paramCount))
    }

    fun toParamString(): String {
        val s = StringBuilder()
        var index = 0
        params.sliceArray(IntRange(0, paramCount)).forEach {
            s.append(it)
            if (index != paramCount) {
                s.append(";")
            }
            index++
        }

        return s.toString()
    }
}