package terminal.service

import androidx.compose.material.Colors
import androidx.compose.ui.text.font.FontFamily

class ConfigService(
    override var maxRows: Int,
    override var maxColumns: Int,
    override var title: String,
    override var colors: Colors,
    override var fontFamily: FontFamily
) : IConfigService