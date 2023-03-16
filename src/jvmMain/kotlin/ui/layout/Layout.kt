package ui.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


class SplitterStatee {
    var isResizing by mutableStateOf(false)
}

class PanelState {
    var height by mutableStateOf(100.dp)
    var with by mutableStateOf(100.dp)
    val horizontalSplitter = SplitterStatee()
    val verticalSplitter = SplitterStatee()
}

//
@Composable
fun FourConrnerLayout(
    topLeft: @Composable () -> Unit,
    topRight: @Composable () -> Unit,
    bottomLeft: @Composable () -> Unit,
    bottomRight: @Composable () -> Unit,
    panelState: PanelState = PanelState()
) = Layout(
    {
        topLeft()
        topRight()
        bottomLeft()
        bottomRight()
        VerticalSplitter1(panelState.verticalSplitter) {
            panelState.with += it
        }
        HorizontalSplitter(panelState.horizontalSplitter) {
            panelState.height += it
        }
    }, measurePolicy = FourCornerLayoutPolicy
)


private object FourCornerLayoutPolicy : MeasurePolicy {
    override fun MeasureScope.measure(measurables: List<Measurable>, constraints: Constraints): MeasureResult {
        check(measurables.size == 6)
        val element1Placeable = measurables[0].measure(constraints)
        val leftWidth = element1Placeable.width
        val rightWidth = constraints.maxWidth - leftWidth
        val topHeight = element1Placeable.height
        val bottomHeight = constraints.maxHeight - topHeight

        val element2Placeable = measurables[1].measure(
            constraints.copy(
                minHeight = topHeight,
                minWidth = rightWidth,
                maxWidth = rightWidth,
                maxHeight = topHeight
            )
        )
        val element3Placeable = measurables[2].measure(
            constraints.copy(
                minWidth = leftWidth,
                maxWidth = leftWidth,
                minHeight = bottomHeight,
                maxHeight = bottomHeight
            )
        )
        val element4Placeable = measurables[3].measure(
            constraints.copy(
                minWidth = rightWidth,
                maxWidth = rightWidth,
                minHeight = bottomHeight,
                maxHeight = bottomHeight
            )
        )
        val element5Placeable = measurables[4].measure(constraints)
        val element6Placeable = measurables[5].measure(constraints)


        return layout(constraints.maxWidth, constraints.maxHeight) {
            element1Placeable.place(0, 0)
            element2Placeable.place(leftWidth, 0)
            element3Placeable.place(0, topHeight)
            element4Placeable.place(leftWidth, topHeight)
            element5Placeable.place(leftWidth, 0)
            element6Placeable.place(0, topHeight)
        }
        // 1
        // |`````|
        // |  1  |
        // |_____|
        // 2
        // |`````````|    |````|````|
        // |1        |    |1   |2   |
        // |_________|    |    |    |
        // |2        |    |    |    |
        // |_________|    |____|____|
        // 3
        // |```|`````|
        // |1  |3    |
        // |___|     |
        // |2  |     |
        // |___|_____|

        // |`````````|
        // |1        |
        // |_________|
        // |2  |3    |
        // |___|_____|

        // |```|`````|
        // |1  | 2   |
        // |___|_____|
        // |3        |
        // |_________|

        // |```|`````|
        // |1  | 2   |
        // |   |_____|
        // |   |3    |
        // |___|_____|

        // 4
        // |```|`````|
        // |1  | 2   |
        // |___|_____|
        // |4  |3    |
        // |___|_____|
    }
}

// 垂直分割线
@Composable
fun VerticalSplitter1(
    splitterState: SplitterStatee,
    onResize: (delta: Dp) -> Unit,
) = Box() {
    val density = LocalDensity.current
    Box(

        modifier = Modifier.width(10.dp).fillMaxHeight().background(Color.Black)
            .draggable(state = rememberDraggableState {
                with(density) {
                    onResize(it.toDp())
                }
            },
                orientation = Orientation.Horizontal,
                startDragImmediately = true,
                onDragStarted = { splitterState.isResizing = true },
                onDragStopped = { splitterState.isResizing = false })
    )
}

// 水平分割线
@Composable
fun HorizontalSplitter(splitterState: SplitterStatee, onResize: (delta: Dp) -> Unit) =
    Box {
        val density = LocalDensity.current
        Box(
            modifier = Modifier.height(10.dp).fillMaxWidth().background(Color.White)
                .draggable(state = rememberDraggableState {
                    with(density) {
                        onResize(it.toDp())
                    }
                },
                    orientation = Orientation.Vertical,
                    startDragImmediately = true,
                    onDragStarted = { splitterState.isResizing = true },
                    onDragStopped = { splitterState.isResizing = false })
        )
    }
