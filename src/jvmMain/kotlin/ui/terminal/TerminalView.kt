package ui.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
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

//    Surface(modifier = Modifier.padding(4.dp)) {
    SelectionContainer(modifier = Modifier.background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().background(AppTheme.colors.material.background)
                .pointerInput(terminal) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitScrollEvent()
                            val y = event.changes[0].scrollDelta.y
                            if (y.compareTo(0) != 0) {
                                if (terminal.bufferService.getActiveBuffer()
                                        .lineCount() < terminal.terminalConfig.rows
                                ) {
                                    continue
                                }
                                terminal.scrollState.y += y.toInt()
                                if (terminal.scrollState.y < 0) {
                                    terminal.scrollState.y = 0
                                }
                                if (terminal.bufferService.getActiveBuffer()
                                        .lineCount() - terminal.scrollState.y < terminal.terminalConfig.rows
                                ) {
                                    terminal.scrollState.y = terminal.bufferService.getActiveBuffer()
                                        .lineCount() - terminal.terminalConfig.rows
                                }
                            }
                        }
                    }
                },
        ) {
            with(terminal.bufferService.getActiveBuffer()) {
                val lines = getLines(
                    IntRange(
                        terminal.scrollState.y,
                        terminal.scrollState.y + terminal.terminalConfig.rows
                    )
                )
                for (index in lines.indices) {
                    Row {
//                            Text("$index")
                        Line(
                            lines[index],
                            terminal.scrollState.y + index == terminal.scrollY + terminal.cursorY,
                            terminal.cursorX
                        )
                    }
                }
            }
        }
    }
}
//}


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
