package ui.session

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import config.Session
import platform.redBorder
import ui.ResizablePanel
import ui.icon.AppIcon
import ui.layout.SplitterState

class SessionSelectionState {
    var expandedSize by mutableStateOf(300.dp)
    var isExpanded by mutableStateOf(true)
    val collapsedSize = 24.dp
    val splitterState by mutableStateOf(SplitterState())
    val expandedSizeMin = 90.dp
}

@Composable
fun SessionSelection(
    sessions: List<Session>,
    sessionSelectionState: SessionSelectionState,
    onAddClick: () -> Unit,
    onSessionDoubleClick: (s: Session) -> Unit
) {
    var selected by remember { mutableStateOf<Session?>(null) }
    var sessionSelectionMenuOpen by remember { (mutableStateOf(false)) }
    var editSessionDialogOpen by remember { mutableStateOf(false) }

    val animatedSize = if (sessionSelectionState.splitterState.isResizing) {
        if (sessionSelectionState.isExpanded) sessionSelectionState.expandedSize else sessionSelectionState.collapsedSize
    } else {
        animateDpAsState(
            if (sessionSelectionState.isExpanded) sessionSelectionState.expandedSize else sessionSelectionState.collapsedSize,
            SpringSpec(stiffness = Spring.StiffnessMedium)
        ).value
    }
    ResizablePanel(
        modifier = Modifier.width(animatedSize).onPreviewKeyEvent {
            true
        }.fillMaxHeight(), sessionSelectionState
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            SessionSelectionTabView(onAddClick)
            val model by remember {
                mutableStateOf(
                    SessionTreeViewModel(
                        buildSessionTreeViewNodeModelFromSessionTreeNode(
                            buildSessionTreeNodeFromSessionList(sessions)
                        )
                    )
                )
            }
            SessionTreeView(model.getSessionList(),
                sessionSelected = {
                    selected = it
                }, rightClickSession = {
                    selected = it
                    sessionSelectionMenuOpen = true
                },
                doubleClickSession = onSessionDoubleClick
            )
            SessionSelectionStatusView(selected)
        }

        SessionRightClickMenu(
            sessionSelectionMenuOpen,
            onDismissRequest = { sessionSelectionMenuOpen = false },
            clickOpen = { sessionSelectionMenuOpen = false },
            clickEdit = {
                editSessionDialogOpen = true
                sessionSelectionMenuOpen = false
            },
            clickDelete = { sessionSelectionMenuOpen = false })

        Dialog(
            onCloseRequest = { editSessionDialogOpen = false },
            visible = editSessionDialogOpen,
            title = "EDIT",
            icon = AppIcon(),
            state = rememberDialogState(width = 400.dp, height = 500.dp),
        ) {
            selected?.let { UpdateSession(it) }
        }

    }
}

@Composable
fun SessionRightClickMenu(
    sessionSelectionMenuOpen: Boolean,
    onDismissRequest: () -> Unit,
    clickOpen: () -> Unit,
    clickEdit: () -> Unit,
    clickDelete: () -> Unit
) {
    CursorDropdownMenu(
        sessionSelectionMenuOpen,
        modifier = Modifier.padding(0.dp),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.padding(0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SessionEditTextButton(onClick = clickOpen, "OPEN")
            SessionEditTextButton(onClick = clickEdit, "EDIT")
            SessionEditTextButton(onClick = clickDelete, "DELETE")
        }
    }
}

@Composable
fun SessionEditTextButton(
    onClick: () -> Unit,
    text: String
) = TextButton(
    onClick = onClick,
    modifier = Modifier.padding(0.dp).fillMaxSize(),
) {
    Text(
        modifier = Modifier.padding(0.dp).fillMaxSize(),
        text = text, fontSize = 12.sp
    )
}
