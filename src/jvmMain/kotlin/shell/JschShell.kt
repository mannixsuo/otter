package shell

import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class JschShell(val host: String, val port: Int, val userName: String, val password: String) : Shell {
    private val inputStreamReader: InputStreamReader
    private val outputStreamWriter: OutputStreamWriter
    val jsch = JSch()
    val session = jsch.getSession(userName, host, port)

    init {
        session.setPassword(password)
        session.userInfo = UserInfoImpl()
        session.connect()
        val channel = session.openChannel("shell") as ChannelShell
        channel.setPtyType("vt100")
        channel.setPtySize(110, 30, 0, 0)
        channel.connect()
        inputStreamReader = InputStreamReader(channel.inputStream)
        outputStreamWriter = OutputStreamWriter(channel.outputStream)
    }


    override fun getChannelInputStreamReader(): InputStreamReader {
        return inputStreamReader
    }

    override fun getChannelOutputStreamWriter(): OutputStreamWriter {
        return outputStreamWriter
    }

    override fun close() {
        session.disconnect()
    }
}