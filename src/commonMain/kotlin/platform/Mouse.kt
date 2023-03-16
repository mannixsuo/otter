package platform

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

expect fun Modifier.cursorForHorizontalResize(): Modifier

expect fun Modifier.cursorForVerticalResize(): Modifier

expect fun Modifier.pointerMoveFilter(
    onEnter: () -> Boolean = { true },
    onExit: () -> Boolean = { true },
    onMove: (Offset) -> Boolean = { true }
): Modifier

expect fun Modifier.onRightClick(action: () -> Unit): Modifier