package ui.terminal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import terminal.ILine
import terminal.service.CursorService
import terminal.service.IBufferService
import terminal.service.IConfigService

private suspend fun AwaitPointerEventScope.awaitScrollEvent(): PointerEvent {
    var event: PointerEvent
    do {
        event = awaitPointerEvent()
    } while (event.type != PointerEventType.Scroll)
    return event
}


@Composable
fun TerminalView(
    focused: () -> Unit,
    focusedOut: () -> Unit,
    screenLines: List<ILine>,
    config: IConfigService,
    bufferService: IBufferService,
    cursorService: CursorService
) {

    SelectionContainer(modifier = Modifier.onFocusChanged {
        if (it.isFocused) {
            focused()
        } else {
            focusedOut()
        }
    }) {
        Column(
            modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitScrollEvent()
                            val y = event.changes[0].scrollDelta.y
                            if (y.compareTo(0) != 0) {
                                if (bufferService.activeBuffer
                                        .lineCount() < config.maxRows
                                ) {
                                    continue
                                }
                                cursorService.down(y.toInt())
                                if (cursorService.scrollY < 0) {
                                    cursorService.scrollY = 0
                                }
                                if (bufferService.activeBuffer
                                        .lineCount() - cursorService.scrollY < config.maxRows
                                ) {
                                    cursorService.scrollY = bufferService.activeBuffer.lineCount() - config.maxRows
                                }
                            }
                        }
                    }
                },
        ) {
            for (index in screenLines.indices) {
                Row {
                    Line(
                        screenLines[index],
                        cursorService.scrollY + index == cursorService.getAbsoluteRowNumber(),
                        cursorService.cursorX,
                        config.fontFamily,
                        cursorService.showCursor
                    )
                }
            }

        }
    }
}


// cursorBlink: () -> Boolean : use function so only rows that cursor affects repaint every time cursor blink
@Composable
fun Line(line: ILine, cursorOnThisLine: Boolean, cursorX: Int, fontFamily: FontFamily, showCursor: Boolean) {
    LineContent(line, cursorOnThisLine, cursorX, fontFamily, showCursor)
}

@Composable
fun LineContent(line: ILine, cursorOnThisLine: Boolean, cursorX: Int, fontFamily: FontFamily, showCursor: Boolean) {
    Row {
        Text(
            text = line.toAnnotatedString(cursorOnThisLine, cursorX, MaterialTheme.colors, showCursor),
            fontFamily = fontFamily,
            softWrap = false
        )

        if (cursorOnThisLine && showCursor) {
            val cursorInText: Boolean = line.length() != 0 && cursorX < line.length()
            if (!cursorInText) {
                for (index in line.length() until cursorX) {
                    Text(" ")
                }
                Text(
                    text = "_",
                )
            }
        }
    }

}
