package shell

import java.io.InputStreamReader
import java.io.OutputStreamWriter

interface Shell {

    fun getChannelInputStreamReader(): InputStreamReader

    fun getChannelOutputStreamWriter(): OutputStreamWriter

    fun close()

}