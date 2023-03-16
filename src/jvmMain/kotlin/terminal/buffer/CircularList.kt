package terminal.buffer


class CircularList<T> {

    private lateinit var buffer: Array<Any?>

    var startIndex = 0

    var length = 0
        set(newLength) {
            if (newLength > field) {
                for (i in 0 until newLength) {
                    buffer[i] = null
                }
            }
            field = newLength
        }

    private var maxLength = 0
        set(newMaxLength) {
            if (newMaxLength == field) {
                return
            }
            // Reconstruct array, starting at index 0. Only transfer values from the
            // indexes 0 to length.
            val newArray = Array<Any?>(newMaxLength) { null }
            val minLength = newMaxLength.coerceAtMost(this.maxLength)
            for (i in 0 until minLength) {
                newArray[i] = this.buffer[getCyclicIndex(i)]
            }
            this.buffer = newArray
            field = newMaxLength
            this.startIndex = 0
        }

    constructor()

    constructor(size: Int) {
        this.maxLength = size
        this.buffer = Array(size) { null }
    }

    /**
     * gets a value from circular list
     */
    fun get(index: Int): T? {
        return this.buffer[getCyclicIndex(index)] as T?
    }

    /**
     * gets a value at index on circular list
     */
    fun set(index: Int, value: T?) {
        this.buffer[getCyclicIndex(index)] = value
    }

    /**
     * Pushes a new value onto the list, wrapping around to the start of the array, overriding index 0
     * if the maximum length is reached.
     * @param value The value to push onto the list.
     */
    fun push(value: T) {
        this.buffer[getCyclicIndex(length)] = value
        if (this.length == this.maxLength) {
            this.startIndex = ++this.startIndex % this.maxLength
        } else {
            this.length++
        }
    }

    /**
     * pop the element at last of the array
     */
    fun pop(): T? {
        return this.buffer[this.getCyclicIndex(this.length-- - 1)] as T?
    }

    private fun getCyclicIndex(index: Int): Int {
        return (startIndex + index) % this.maxLength
    }

    override fun toString(): String {
        val builder = StringBuffer()
        for (element in buffer) {
            builder.append(element.toString())
            builder.append("\n")
        }
        return builder.toString()
    }


}