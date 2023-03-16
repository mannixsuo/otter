package platform

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import java.awt.Cursor

// set cursor type
actual fun Modifier.cursorForHorizontalResize(): Modifier =
    this.pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))

actual fun Modifier.cursorForVerticalResize(): Modifier =
    this.pointerHoverIcon(PointerIcon(Cursor(Cursor.N_RESIZE_CURSOR)))

@OptIn(ExperimentalComposeUiApi::class)
actual fun Modifier.pointerMoveFilter(
    onEnter: () -> Boolean,
    onExit: () -> Boolean,
    onMove: (Offset) -> Boolean
): Modifier = this.onPointerEvent(PointerEventType.Enter) { onEnter() }
    .onPointerEvent(PointerEventType.Exit) { onExit() }
    .onPointerEvent(PointerEventType.Move) { event -> onMove(event.changes.first().position) }

actual fun Modifier.onRightClick(action: () -> Unit): Modifier = this.pointerInput(MutableInteractionSource()) {
    while (currentCoroutineContext().isActive) {
        awaitPointerEventScope {
            val event = awaitPointerEvent(PointerEventPass.Main)
            if (event.buttons.isSecondaryPressed) {
                action()
            }
        }
    }
}