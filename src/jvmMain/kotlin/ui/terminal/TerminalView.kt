package ui.terminal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import terminal.ILine
import terminal.service.IBufferService
import terminal.service.IConfigService
import terminal.service.ICursorService
import ui.font.Fonts.jetbrainsMono

private suspend fun AwaitPointerEventScope.awaitScrollEvent(): PointerEvent {
    var event: PointerEvent
    do {
        event = awaitPointerEvent()
    } while (event.type != PointerEventType.Scroll)
    return event
}


@Composable
fun TerminalView(
    version: Int,
    config: IConfigService,
    bufferService: IBufferService,
    cursorService: ICursorService
) {
    val state: MutableState<List<ILine>?> = remember { mutableStateOf(null, neverEqualPolicy()) }

    SelectionContainer() {
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
            with(bufferService.activeBuffer) {
                state.value = getLines(
                    IntRange(
                        cursorService.scrollY,
                        cursorService.getAbsoluteRowNumber() + 1
                    )
                )
                state.value?.let {
                    for (index in it.indices) {
                        Row {
                            Line(
                                it[index],
                                cursorService.scrollY + index == cursorService.getAbsoluteRowNumber(),
                                cursorService.scrollX
                            )
                        }
                    }
                }

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
            text = line.toAnnotatedString(cursorOnThisLine, cursorX, MaterialTheme.colors),
            fontFamily = jetbrainsMono(),
            softWrap = false
        )

        if (cursorOnThisLine) {
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
