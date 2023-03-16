package ui.icon

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp

@Composable
fun EmptyBoxIcon() = Box(modifier = Modifier.size(24.dp))

@Composable
fun FolderIcon() = Box(modifier = Modifier.size(24.dp).padding(4.dp)) {
    Icon(
        BitmapPainter(useResource("icon/folder.png", ::loadImageBitmap)),
        contentDescription = null,
        tint = LocalContentColor.current,
    )
}

@Composable
fun ServerIcon() = Box(modifier = Modifier.size(24.dp).padding(4.dp)) {
    Icon(
        BitmapPainter(useResource("icon/server.png", ::loadImageBitmap)),
        contentDescription = null,
        tint = LocalContentColor.current
    )
}

@Composable
fun ArrowDownIcon(modifier: Modifier) = Box(modifier = modifier.size(24.dp)) {
    Icon(
        Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LocalContentColor.current
    )
}

@Composable
fun ArrowRightIcon(modifier: Modifier) = Box(modifier = modifier.size(24.dp)) {
    Icon(
        Icons.Default.KeyboardArrowRight, contentDescription = null, tint = LocalContentColor.current
    )
}

fun AppIcon() = BitmapPainter(useResource("ic_launcher.png", ::loadImageBitmap))
