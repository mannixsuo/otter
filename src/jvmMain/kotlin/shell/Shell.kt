package shell

import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

interface Shell {

    fun getChannelInputStreamReader(): InputStreamReader

    fun getChannelInputStream(): InputStream

    fun getChannelOutputStreamWriter(): OutputStreamWriter

    fun close()

}