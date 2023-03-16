package ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import terminal.ILine

class SingleSelection {
    var selected: Any? by mutableStateOf(null)
}


class ActiveTerminalScreen {

    var screenLines: List<ILine> by mutableStateOf(emptyList(), neverEqualPolicy())

    var cursorX: Int by mutableStateOf(0)

    var cursorY: Int by mutableStateOf(0)

}