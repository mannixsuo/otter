package terminal.service

import parser.SingleCharacterFunProcessor
import terminal.CSIProcessor
import terminal.EscProcessor

interface ITerminalInputProcessorService {

    val csiProcessor: CSIProcessor

    val singleCharacterFunProcessor: SingleCharacterFunProcessor

    val escProcessor: EscProcessor
}