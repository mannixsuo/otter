package character

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import parser.ParserAction
import parser.ParserState

class TransitionTableTest : DescribeSpec({
    val table = TransitionTable(4096)
    table.setDefault(ParserAction.IGNORE, ParserState.GROUND)
    describe("query table test") {
        it("escape test") {
            table.add(0x1b, ParserState.GROUND, ParserAction.CLEAR, ParserState.ESCAPE)
            with(table.queryTable(0x1b, ParserState.GROUND)) {
                first shouldBe ParserAction.CLEAR
                second shouldBe ParserState.ESCAPE
            }
        }

        it("csi test") {
            table.add(0x5b, ParserState.ESCAPE, ParserAction.CLEAR, ParserState.CSI_ENTRY)
            with(table.queryTable(0x5b, ParserState.ESCAPE)) {
                first shouldBe ParserAction.CLEAR
                second shouldBe ParserState.CSI_ENTRY
            }
        }

        it("print test") {
            table.addRange(0x20, 0x7f, ParserState.GROUND, ParserAction.PRINT, ParserState.GROUND)
            with(table.queryTable(0x20, ParserState.GROUND)) {
                first shouldBe ParserAction.PRINT
                second shouldBe ParserState.GROUND
            }
            with(table.queryTable(0x21, ParserState.GROUND)) {
                first shouldBe ParserAction.PRINT
                second shouldBe ParserState.GROUND
            }
            with(table.queryTable(0x7f, ParserState.GROUND)) {
                first shouldBe ParserAction.PRINT
                second shouldBe ParserState.GROUND
            }
        }
    }

})