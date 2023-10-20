package cc.suffro.scannergenerator

import STANDARD_INDENTATION
import cc.suffro.scannergenerator.data.TokenType
import kotlin.math.max

const val EMPTY_LINE = ""

fun Pair<Int, String>.addIndentation(): String {
    return " ".repeat(first) + second
}

fun String.addIndentation(indentation: Int): String {
    return let { " ".repeat(indentation) + it }
}

fun determineAndCreateClosingBrackets(
    codeLines: Sequence<String>,
    attachSingleLine: Boolean = true
): Sequence<String> {
    val openingBracketCount = codeLines.count { it.contains("{") }
    val closingBracketCount = codeLines.count { it.contains("}") }
    val bracketCountDifference = openingBracketCount - closingBracketCount

    val closingBrackets = sequence {
        repeat(bracketCountDifference) {
            yield("}")
        }
    }

    return if (attachSingleLine) {
        closingBrackets + sequenceOf(EMPTY_LINE)
    } else {
        closingBrackets
    }
}


fun Sequence<String>.determineAndCreateClosingBracketsExtension(attachSingleLine: Boolean = true): Sequence<String> =
    this + determineAndCreateClosingBrackets(this, attachSingleLine)


fun List<Pair<TokenType, String>>.values() = map { it.second }

fun indent(code: Sequence<String>, baseIndentation: Int = 0): String {
    val codeLines = code.toMutableList()
    var indentation = baseIndentation

    codeLines.forEachIndexed { lineNumber, line ->
        val trimmedLine = line.trim()
        if (trimmedLine.isNotEmpty()) {
            if (isSingleBracket(trimmedLine)) {
                codeLines[lineNumber] = trimmedLine.addIndentation(max(0, indentation - STANDARD_INDENTATION))
            } else {
                codeLines[lineNumber] = trimmedLine.addIndentation(max(0, indentation))
            }
            indentation = calculateNewIndentation(trimmedLine, indentation)
        } else {
            codeLines[lineNumber] = EMPTY_LINE
        }
    }

    return codeLines.joinToString(separator = "\n")
}

private fun isSingleBracket(trimmedLine: String): Boolean {
    return ("}])".contains(trimmedLine.first()))
}

private fun calculateNewIndentation(trimmedLine: String, indentation: Int): Int {
    val reducedLine = trimmedLine.filterForQuotes("\"").filterForQuotes("'")
    return indentation + (reducedLine.count { "{([".contains(it) } - reducedLine.count { "}])".contains(it) }) * STANDARD_INDENTATION
}

private fun String.filterForQuotes(delimiter: String): String {
    return split(delimiter).filter { it.lastOrNull() != '\\' }.filterIndexed { index, _ -> index % 2 == 0 }
        .joinToString("")
}
