package terminal.service

import androidx.compose.ui.graphics.Color
import ui.AppTheme

class CharacterService {
    private var nextCharFgColor = AppTheme.colors.material.primary
    private var nextCharBgColor = AppTheme.colors.material.background
    private var nextCharBold = false
    private var nextCharItalic = false

    fun getCharBg(): Color {
        return nextCharBgColor
    }

    fun getCharFg(): Color {
        return nextCharFgColor
    }

    fun getCharBold(): Boolean {
        return nextCharBold
    }

    fun getCharItalic(): Boolean {
        return nextCharItalic
    }

    fun normal() {
        nextCharFgColor = AppTheme.colors.material.primary
        nextCharBgColor = AppTheme.colors.material.background
        nextCharBold = false
        nextCharItalic = false
    }

    fun bold() {
        nextCharBold = true
    }

    fun italicized() {
        nextCharItalic = true
    }

    fun fgYellow() {
        nextCharFgColor = AppTheme.colors.yellow
    }

    fun fgDefault() {
        nextCharFgColor = AppTheme.colors.material.primary
    }
}