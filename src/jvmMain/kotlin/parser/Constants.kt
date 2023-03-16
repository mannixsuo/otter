package parser

/**
 * https://vt100.net/emu/dec_ansi_parser
 */

enum class ParserState(val state: Int) {
    /**
     *   This is the initial state of the parser, and the state used to consume all characters other than components
     *   of escape and control sequences.
     */
    GROUND(0),

    /**
     * This state is entered whenever the C0 control ESC is received.
     */
    ESCAPE(1),

    /**
     * This state is entered when an intermediate character arrives in an escape sequence.
     */
    ESCAPE_INTERMEDIATE(2),

    /**
     * This state is entered when the control function CSI is recognised, in 7-bit or 8-bit form.
     */
    CSI_ENTRY(3),

    /**
     * This state is entered when a parameter character is recognised in a control sequence.
     */
    CSI_PARAM(4),

    /**
     * This state is entered when an intermediate character is recognised in a control sequence.
     */
    CSI_INTERMEDIATE(5),

    /**
     * This state is used to consume remaining characters of a control sequence that is still being recognised,
     * but has already been disregarded as malformed.
     */
    CSI_IGNORE(6),

    /**
     * This state is entered when the control function DCS is recognised, in 7-bit or 8-bit form.
     */
    DCS_ENTRY(7),

    /**
     * This state is entered when a parameter character is recognised in a device control string.
     */
    DCS_PARAM(8),

    /**
     * This state is entered when an intermediate character is recognised in a device control string.
     */
    DCS_INTERMEDIATE(9),

    /**
     * This state is a shortcut for writing state machines for all possible device control strings into the main parser.
     */
    DCS_PASS_THROUGH(10),

    /**
     * This state is used to consume remaining characters of a device control string that is still being recognised,
     * but has already been disregarded as malformed. This state will only exit when the control function ST is
     * recognised, at which point it transitions to ground state.
     */
    DCS_IGNORE(11),

    /**
     * This state is entered when the control function OSC (Operating System Command) is recognised.
     */
    OSC_STRING(12),

    /**
     * The VT500 doesn’t define any function for these control strings, so this state ignores all received characters
     * until the control function ST is recognised.
     */
    SOS_PM_APC_STRING(13),
    ;

    companion object {
        fun of(x: Int): ParserState {
            return ParserState.values()[x]
        }
    }


}


/**
 * Internal actions of EscapeSequenceParser.
 */

enum class ParserAction(val action: Int) {

    /**
     * The character or control is not processed. No observable difference in the terminal’s state would occur if the
     * character that caused this action was not present in the input stream.
     * (Therefore, this action can only occur within a state.)
     */
    IGNORE(0),

    ERROR(1),

    /**
     * This action only occurs in ground state.
     * The current code should be mapped to a glyph according to the character set mappings and shift states in effect,
     * and that glyph should be displayed.
     * 20 (SP) and 7F (DEL) have special behaviour in later VT series, as described in ground.
     */
    PRINT(2),

    /**
     * The C0 or C1 control function should be executed,
     * which may have any one of a variety of effects,
     * including changing the cursor position,
     * suspending or resuming communications or changing the shift states in effect.
     * There are no parameters to this action.
     */
    EXECUTE(3),

    /**
     * This action causes the current private flag, intermediate characters,
     * final character and parameters to be forgotten.
     * This occurs on entry to the escape, csi entry and dcs entry states,
     * so that erroneous sequences like CSI 3 ; 1 CSI 2 J are handled correctly.
     */
    CLEAR(4),

    /**
     * When the control function OSC (Operating System Command) is recognised,
     * this action initializes an external parser (the “OSC Handler”) to handle the characters from the control string.
     * OSC control strings are not structured in the same way as device control strings, so there is no choice of parsers.
     */
    OSC_START(5),

    /**
     * This action passes characters from the control string to the OSC Handler as they arrive.
     * There is therefore no need to buffer characters until the end of the control string is recognised.
     */
    OSC_PUT(6),

    /**
     * This action is called when the OSC string is terminated by ST, CAN, SUB or ESC, to allow the OSC handler to finish neatly.
     */
    OSC_END(7),

    /**
     * A final character has arrived, so determine the control function to be executed from private marker,
     * intermediate character(s) and final character, and execute it, passing in the parameter list.
     * The private marker and intermediate characters are available because collect stored them as they arrived.
     */
    CSI_DISPATCH(8),

    /**
     * This action collects the characters of a parameter string for a control sequence or device control sequence and builds a list of parameters.
     */
    PARAM(9),

    /**
     * The private marker or intermediate character should be stored for later use in selecting a control function to be executed when a final character arrives.
     * X3.64 doesn’t place any limit on the number of intermediate characters allowed before a final character,
     * although it doesn’t define any control sequences with more than one.
     * Digital defined escape sequences with two intermediate characters,
     * and control sequences and device control strings with one.
     * If more than two intermediate characters arrive,
     * the parser can just flag this so that the dispatch can be turned into a null operation.
     */
    COLLECT(10),

    /**
     * The final character of an escape sequence has arrived,
     * so determined the control function to be executed from the intermediate character(s) and final character, and execute it.
     * The intermediate characters are available because collect stored them as they arrived.
     */
    ESC_DISPATCH(11),

    /**
     * This action is invoked when a final character arrives in the first part of a device control string.
     * It determines the control function from the private marker, intermediate character(s) and final character, and executes it, passing in the parameter list.
     * It also selects a handler function for the rest of the characters in the control string.
     * This handler function will be called by the put action for every character in the control string as it arrives.
     */
    DCS_HOOK(12),

    /**
     * This action passes characters from the data string part of a device control string to a handler that has previously been selected by the hook action.
     * C0 controls are also passed to the handler.
     */
    DCS_PUT(13),

    /**
     * When a device control string is terminated by ST, CAN, SUB or ESC,
     * this action calls the previously selected handler function with an “end of data” parameter.
     * This allows the handler to finish neatly.
     */
    DCS_UNHOOK(14), ;


    companion object {
        fun of(i: Int): ParserAction {
            return ParserAction.values()[i]
        }
    }
}

enum class OscState {
    START, ID, PAYLOAD, ABORT,
}

// payload limit for OSC and DCS
const val PAYLOAD_LIMIT = 10000000