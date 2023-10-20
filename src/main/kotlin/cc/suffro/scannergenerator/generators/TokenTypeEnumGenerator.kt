package cc.suffro.scannergenerator.generators

import cc.suffro.scannergenerator.data.Generator
import cc.suffro.scannergenerator.data.TokenType
import cc.suffro.scannergenerator.determineAndCreateClosingBrackets
import java.util.*

object TokenTypeEnumGenerator : Generator {
    fun generate(): Sequence<String> {
        val uppercaseKeywords =
            TokenType.values().sortedBy { it.clazz }.map { it.name.uppercase(Locale.getDefault()) }
        val code = sequenceOf("enum class TokenType {") + uppercaseKeywords.map { "$it," }

        return code + determineAndCreateClosingBrackets(code)
    }
}