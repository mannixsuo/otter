package terminal.buffer

import androidx.compose.ui.graphics.Color


val defaultTheme = Theme()

class Theme {

    val colors = Colors()

    class Colors {

        val black = Color.Black
        val red = Color.Red
        val green = Color.Green
        val yellow = Color.Yellow
        val blue = Color.Blue
        val magenta = Color.Magenta
        val cyan = Color.Cyan
        val white = Color.White
        val defaultForeground = white
        val defaultBackground = black

    }

}