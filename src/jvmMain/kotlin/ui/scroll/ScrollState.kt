package ui.scroll

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ScrollState {
    var y by mutableStateOf(0)
    var x by mutableStateOf(0)
}