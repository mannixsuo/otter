package terminal.service

import androidx.compose.material.Colors
import androidx.compose.ui.text.font.FontFamily

/**
 * config of the terminal
 */
interface IConfigService {

    var maxRows: Int

    var maxColumns: Int

    var title: String

    var colors: Colors

    var fontFamily: FontFamily

}