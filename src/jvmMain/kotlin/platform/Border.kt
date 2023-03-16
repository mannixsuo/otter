package platform

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

actual fun Modifier.testBorder(): Modifier = Modifier.border(BorderStroke(1.dp, Color.White))
actual fun Modifier.redBorder(): Modifier = Modifier.border(BorderStroke(1.dp, Color.Red))
actual fun Modifier.blueBorder(): Modifier = Modifier.border(BorderStroke(1.dp, Color.Blue))
actual fun Modifier.greenBorder(): Modifier = Modifier.border(BorderStroke(1.dp, Color.Green))
actual fun Modifier.yellowBorder(): Modifier = Modifier.border(BorderStroke(1.dp, Color.Yellow))
actual fun Modifier.grayBorder(): Modifier = Modifier.border(BorderStroke(1.dp, Color.Gray))