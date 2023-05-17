package config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import utils.getUserHomeDir
import java.io.File
import java.io.FileWriter

private val json = Json { prettyPrint }

@Serializable
data class Session(
    var name: String,
    var group: String,
    var type: String,
    var description: String?,
    var shell: Shell?,
    var ssh: Ssh?,
)

@Serializable
data class Shell(
    var command: String
)

@Serializable
data class Ssh(
    var host: String,
    var port: Int,
    var termType: String,
    val user: String,
    val password: String
)

@Serializable
data class RemoteSession(
    val name: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String
)

@Serializable
data class LocalSession(
    val name: String,
    val commands: List<String>
)

@Serializable
data class Theme(val colors: Color)

@Serializable
data class Color(
    var black: String = "0xFF000000",
    var red: String = "0xFFFF0000",
    var green: String = "0xFF00FF00",
    var yellow: String = "0xFFFFFF00",
    var blue: String = "0xFF0000FF",
    var magenta: String = "0xFFFF00FF",
    var cyan: String = "0xFF00FFFF",
    var white: String = "0xFFFFFFFF"
)

const val DEFAULT_CONFIG_FILE_PATH = "/.otter/settings.json"

@Serializable
data class Ui(val columns: Int = 120, val rows: Int = 30, val theme: Theme)

@Serializable
data class TerminalConfig(
    val sessionList: MutableList<Session>,
    val ui: Ui
)


fun readConfigFromFile(): TerminalConfig {
    val configFile = File(getUserHomeDir() + DEFAULT_CONFIG_FILE_PATH)
    if (configFile.exists()) {
        val bytes = configFile.inputStream().readAllBytes()
        val string = String(bytes)
        println(string)
        return json.decodeFromString(TerminalConfig.serializer(), string)
    } else {
        val config = TerminalConfig(
            ArrayList(),
            Ui(theme = Theme(Color()))
        )
        if (!configFile.parentFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        val encodeToString = json.encodeToString(TerminalConfig.serializer(), config)
        println(encodeToString)
        val fileWriter = FileWriter(configFile)
        fileWriter.write(encodeToString)
        fileWriter.flush()
        fileWriter.close()
        return config
    }
}


fun writeConfigToFile(config: TerminalConfig) {
    val encodeToString = json.encodeToString(TerminalConfig.serializer(), config)
    val configFile = File(getUserHomeDir() + DEFAULT_CONFIG_FILE_PATH)
    val fileWriter = FileWriter(configFile)
    fileWriter.write(encodeToString)
    fileWriter.flush()
    fileWriter.close()
}