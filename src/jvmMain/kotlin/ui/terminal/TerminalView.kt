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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import terminal.ILine
import ui.AppTheme
import ui.font.Fonts.jetbrainsMono
import java.lang.Math.max


@Composable
fun TerminalView(cursorX: Int = 0, cursorY: Int = 0, lines: List<ILine>) {
    Surface {
        SelectionContainer {
            Column(
                modifier = Modifier.fillMaxWidth().background(AppTheme.colors.material.background),
            ) {
                for (index in lines.indices)
                    Line(lines[index], index == cursorY, cursorX)
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
            text = line.toAnnotatedString(cursorOnThisLine, cursorX),
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
                    modifier = Modifier
                        .drawWithContent {
                            drawRect(Color.White)
                        },
                    text = "_",
                )
            }
        }
    }

}
