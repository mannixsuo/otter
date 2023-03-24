package ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import terminal.ILine
import terminal.Terminal
import ui.AppTheme
import ui.font.Fonts.jetbrainsMono

private suspend fun AwaitPointerEventScope.awaitScrollEvent(): PointerEvent {
    var event: PointerEvent
    do {
        event = awaitPointerEvent()
    } while (event.type != PointerEventType.Scroll)
    return event
}

@Composable
fun TerminalView(terminal: Terminal) {

    Surface {
        SelectionContainer {
            Column(
                modifier = Modifier.fillMaxWidth().background(AppTheme.colors.material.background)
                    .pointerInput(terminal) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitScrollEvent()
                                val y = event.changes[0].scrollDelta.y
                                val x = event.changes[0].scrollDelta.x
                                if (y.compareTo(0) != 0) {
                                    terminal.scrollY += y.toInt()
                                    if (terminal.scrollY < 0) {
                                        terminal.scrollY = 0
                                    }
                                }
                                if (x.compareTo(0) != 0) {
                                    terminal.scrollX += x.toInt()
                                    if (terminal.scrollX < 0) {
                                        terminal.scrollX = 0
                                    }
                                }
                            }
                        }
                    },
            ) {
                for (index in terminal.bufferService.getActiveBuffer()
                    .getLines(
                        IntRange(
                            terminal.scrollY,
                            terminal.scrollY + terminal.terminalConfig.rows
                        )
                    ).indices) Line(
                    terminal.bufferService.getActiveBuffer()
                        .getLines(IntRange(terminal.scrollY, terminal.scrollY + terminal.terminalConfig.rows))[index],
                    index == terminal.cursorY,
                    terminal.cursorX
                )
            }
        }
    }
}


// cursorBlink: () -> Boolean : use function so only rows that cursor affects repaint every time cursor blink
@Composable
fun Line(line: ILine, cursorOnThisLine: Boolean, cursorX: Int) {
    LineContent(line, cursorOnThisLine, cursorX)
}

@Composable
fun LineContent(line: ILine, cursorOnThisLine: Boolean, cursorX: Int) {

    Row {
        Text(
            text = line.toAnnotatedString(cursorOnThisLine, cursorX), fontFamily = jetbrainsMono(), softWrap = false
        )

        if (cursorOnThisLine) {
            val cursorInText: Boolean = line.length() != 0 && cursorX < line.length()
            if (!cursorInText) {
                for (index in line.length() until cursorX) {
                    Text(" ")
                }
                Text(
                    modifier = Modifier.drawWithContent {
                        drawRect(Color.White)
                    },
                    text = "_",
                )
            }
        }
    }

}
