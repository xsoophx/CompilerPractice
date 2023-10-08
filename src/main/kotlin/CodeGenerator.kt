import java.util.Locale
import kotlin.math.max

const val CHECK_AND_CHANGE_FUNCTION_NAME = "checkAndChangeState"
const val STANDARD_INDENTATION = 4

class CodeGenerator(private val keywords: Array<Keyword> = Keyword.values()) {

    private val states = keywords.flatMap(Keyword::splitKeywordToStates).distinct().sorted()

    fun generate(): String {
        val codeLines = generateTokenTypeEnum() +
                generateTokenDataClass() +
                generateStateEnumClass() +
                generateStateAndToken() +
                generateStateMachine()

        return indent(codeLines)
    }

    private fun generateTokenDataClass(): Sequence<String> {
        val code = sequenceOf(
            "data class Token(val type: TokenType, val value: String) {",
            "override fun toString(): String {",
            "return \"Token Type: \$type, Value: \$value\""
        )

        return code + determineAndCreateClosingBrackets(code)
    }

    private fun generateTokenTypeEnum(): Sequence<String> {
        // outsource this
        val dataTypes = listOf("INT", "DOUBLE", "FLOAT", "SHORT", "LONG", "BOOL")

        val uppercaseKeywords = keywords.map { it.name.uppercase(Locale.getDefault()) }
        val uppercaseLiterals = uppercaseKeywords.filter { it in dataTypes }.map { it + "_LITERAL" }

        val code =
            sequenceOf("enum class TokenType {") +
                    (uppercaseKeywords + uppercaseLiterals).sorted().map { "$it," } +
                    // outsource
                    sequenceOf("ASSIGN,", "IDENTIFIER,", "SEMICOLON,")

        return code + determineAndCreateClosingBrackets(code)

    }

    fun generateStateEnumClass(): Sequence<String> {
        val code = sequenceOf("enum class State {") + generateEnumEntries()
        return code + determineAndCreateClosingBrackets(code)
    }

    private fun generateEnumEntries(): Sequence<String> {
        val states = getKeywordStatesAsString() + NonSplittableStates.values()
            .map { it.name.uppercase(Locale.getDefault()) }.sorted()

        return states.map { "$it," }
    }

    private fun getKeywordStatesAsString(): Sequence<String> {
        return states.map { it.uppercase(Locale.getDefault()) }.asSequence()
    }

    private fun generateStateAndToken(): Sequence<String> {
        return sequenceOf(
            "private var currentState = State.START",
            "private var currentToken = StringBuilder()\n"
        )
    }

    private fun generateStateMachine(): Sequence<String> {
        val preCode = sequenceOf(
            "fun readStates(char: Char, tokens: MutableList<Token>) {",
            "when (currentState) {"
        )
        val startStateCases = createTopLevelStateCase()
        return preCode + startStateCases + determineAndCreateClosingBrackets(preCode)
    }

    private fun createTopLevelStateCase(): Sequence<String> {
        val preCode = sequenceOf(
            "State.START -> {",
            "when (char) {"
        )

        val startStateCases = createStartStateCases().flatMap(StartStateCondition::getIfClauseAsSequence).asSequence()
        return preCode + startStateCases + determineAndCreateClosingBrackets(preCode, false)
    }

    fun createStartStateCases(): List<StartStateCondition> {
        val singleCharStates =
            states.filter { it.length == 1 }
                .sorted()
                .associate { CharIfCondition(it.first()) to it.uppercase(Locale.getDefault()) }

        val nonSplittableStates = NonSplittableStates.values()
            .asSequence()
            .filter { it.condition != null }
            .sortedBy { it.hasToBeLast }
            .associate { it.condition!! to it.name.uppercase(Locale.getDefault()) }

        val startStateConditions =
            (singleCharStates + nonSplittableStates).map { (condition, state) ->
                StartStateCondition(condition, state)
            }
        return startStateConditions
    }


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
        return indentation + (trimmedLine.count { "{([".contains(it) } - trimmedLine.count { "}])".contains(it) }) * STANDARD_INDENTATION
    }

    private fun determineAndCreateClosingBrackets(
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
}

enum class NonSplittableStates(val condition: StateIfCondition?, val hasToBeLast: Boolean = false) {
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), true),
    SEMICOLON(CharIfCondition(';')),
    ASSIGN(CharIfCondition('=')),
    START(null);
}
