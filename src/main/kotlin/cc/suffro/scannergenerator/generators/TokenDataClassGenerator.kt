package cc.suffro.scannergenerator.generators

import cc.suffro.scannergenerator.data.Generator
import cc.suffro.scannergenerator.determineAndCreateClosingBrackets

object TokenDataClassGenerator : Generator {
    fun generate(): Sequence<String> {
        val code = sequenceOf(
            "data class Token(val type: TokenType, val value: String) {",
            "override fun toString(): String {",
            "return \"Token Type: \$type, Value: \$value\""
        )

        return code + determineAndCreateClosingBrackets(code)
    }
}