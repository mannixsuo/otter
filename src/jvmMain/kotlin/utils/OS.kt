package utils

import java.util.*


enum class OS {
    WINDOWS, LINUX, MAC, SOLARIS, UNKNOWN
}

fun getOS(): OS {
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    return when {
        os.contains("win") -> {
            OS.WINDOWS
        }
        os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
            OS.LINUX
        }
        os.contains("mac") -> {
            OS.MAC
        }
        os.contains("sunos") -> {
            OS.SOLARIS
        }
        else -> OS.UNKNOWN
    }
}

fun getUserHomeDir(): String {
    val property = System.getProperty("user.home")
    return property
}