package shell

import com.pty4j.PtyProcess
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter

class LocalPty(val ptyProcess: PtyProcess) : Shell {

    private val ptyOutPutStream: OutputStream = ptyProcess.outputStream
    private val ptyOutPutStreamWriter: OutputStreamWriter = OutputStreamWriter(ptyOutPutStream)

    private val ptyInputStream: InputStream = ptyProcess.inputStream
    private val ptyInputStreamReader: InputStreamReader = InputStreamReader(ptyInputStream)


    override fun getChannelInputStreamReader(): InputStreamReader {
        return ptyInputStreamReader
    }

    override fun getChannelOutputStreamWriter(): OutputStreamWriter {
        return ptyOutPutStreamWriter
    }

    override fun close() {
        ptyProcess.destroy()
        println("LocalPty close ${ptyProcess.pid()}")
    }
}