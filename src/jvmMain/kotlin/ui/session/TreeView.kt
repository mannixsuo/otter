package ui.session

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import config.Session
import platform.VerticalScrollbar
import platform.onRightClick
import ui.icon.*
import utils.withoutWidthConstraints
import java.util.*
import java.util.stream.Collectors

@Composable
fun SessionTreeView(
    sessions: List<SessionTreeViewNodeModel>,
    sessionSelected: (s: Session) -> Unit,
    rightClickSession: (s: Session) -> Unit,
    doubleClickSession: (s: Session) -> Unit
) {
    val columnScrollState = rememberLazyListState()
    Box {
        LazyColumn(
            modifier = Modifier.fillMaxSize().withoutWidthConstraints(), state = columnScrollState
        ) {
            items(sessions.size) { it ->
                SessionTreeItemView(
                    model = sessions[it],
                    sessionSelected = sessionSelected,
                    rightClickSession = rightClickSession,
                    doubleClickSession = doubleClickSession
                )
            }
        }
        VerticalScrollbar(
            Modifier.align(Alignment.CenterEnd), columnScrollState
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionTreeItemView(
    model: SessionTreeViewNodeModel,
    sessionSelected: (s: Session) -> Unit,
    rightClickSession: (s: Session) -> Unit,
    doubleClickSession: (s: Session) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .padding(start = 12.dp * model.level)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    model.session.session?.let {
                        sessionSelected(it)
                    }
                },
                onDoubleClick = {
                    if (model.canExpanded) {
                        model.toggleExpand()
                        expanded = !expanded
                    } else {
                        model.session.session?.let { doubleClickSession(it) }
                    }
                }
            ).onRightClick {
                model.session.session?.let { rightClickSession(it) }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (model.canExpanded) {
            if (expanded) {
                ArrowDownIcon(Modifier.clickable {
                    model.toggleExpand()
                    expanded = false
                })
            } else {
                ArrowRightIcon(Modifier.clickable {
                    model.toggleExpand()
                    expanded = true
                })
            }
            FolderIcon()
        } else {
            EmptyBoxIcon()
            ServerIcon()
        }
        Text(model.session.name)
    }
}


class SessionTreeViewModel(private val sessions: List<SessionTreeViewNodeModel> = emptyList()) {
    fun getSessionList(): List<SessionTreeViewNodeModel> {
        val list = mutableListOf<SessionTreeViewNodeModel>()
        for (session in sessions) {
            session.addTo(list)
        }
        return list
    }
}


fun buildSessionTreeNodeFromSessionList(sessionList: List<Session>): List<SessionTreeNode> {
    val sessionTreeNodes = sessionList.stream().map {
        SessionTreeNode(it.name, it)
    }.collect(Collectors.toList())

    // build the tree structure of all the nodes
    val nodeMap = HashMap<String, SessionTreeNode>()
    for (node in sessionTreeNodes) {
        buildNodeTree(node, nodeMap)
    }
    val result: MutableList<SessionTreeNode> = LinkedList()
    // filter root nodes
    for (node in nodeMap.entries) {
        if (!node.key.contains("/")) {
            result.add(node.value)
        }
    }
    return result
}

fun buildNodeTree(node: SessionTreeNode, nodeMap: HashMap<String, SessionTreeNode>) {
    nodeMap[node.name] = node
    if (!node.name.contains("/")) {
        return
    }
    val parentNodeName = node.name.substring(0, node.name.lastIndexOf("/"))
    if (nodeMap.containsKey(parentNodeName)) {
        nodeMap[parentNodeName]?.children?.add(node)
    } else {
        val parentNode = SessionTreeNode(parentNodeName)
        parentNode.children.add(node)
        buildNodeTree(parentNode, nodeMap)
    }

}

fun buildSessionTreeViewNodeModelFromSessionTreeNode(treeNodes: List<SessionTreeNode>): List<SessionTreeViewNodeModel> {
    val result = LinkedList<SessionTreeViewNodeModel>()
    for (node in treeNodes) {
        val viewNode = SessionTreeViewNodeModel(node, groupLevel(node.name))
        result.add(viewNode)
    }
    return result
}

fun groupLevel(name: String): Int {
    var count = 0
    for (s in name) {
        if (s == '/') {
            count++
        }
    }
    return count
}


class SessionTreeNode(
    val name: String, var session: Session? = null, val children: LinkedList<SessionTreeNode> = LinkedList()
)


class SessionTreeViewNodeModel(val session: SessionTreeNode, val level: Int) {

    private var children: List<SessionTreeViewNodeModel> by mutableStateOf(emptyList())

    val canExpanded: Boolean = session.children.isNotEmpty()

    fun toggleExpand() {
        children = if (children.isEmpty()) {
            session.children.map { child -> SessionTreeViewNodeModel(child, level + 1) }
        } else {
            emptyList()
        }
    }

    fun addTo(list: MutableList<SessionTreeViewNodeModel>) {
        list.add(this)
        for (child in children) {
            child.addTo(list)
        }
    }
}