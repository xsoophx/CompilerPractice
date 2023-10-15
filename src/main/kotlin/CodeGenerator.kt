import java.util.Locale
import kotlin.math.max

const val CHECK_AND_CHANGE_FUNCTION_NAME = "checkAndChangeState"
const val STANDARD_INDENTATION = 4

class CodeGenerator(
    private val keywords: Sequence<TokenType> = TokenType.tokenTypeKeywords,
) {

    private val states = keywords.flatMap(TokenType::splitKeywordToStates).distinct().sorted()
    private val statesWithTokenType =
        keywords.map(TokenType::splitKeywordToStatesWithTokenType).reduce { acc, map -> acc + map }
            .toSortedMap()

    fun generate(): String {
        val codeLines = generateTokenTypeEnum() +
                generateTokenDataClass() +
                generateStateEnumClass() +
                generateStateAndToken() +
                generateStateMachine() +
                generateCheckAndChangeStateFunction() +
                generateSetStartStateFunction()

        return indent(codeLines)
    }

    private fun generateSetStartStateFunction(): Sequence<String> {
        return sequenceOf(
            "private fun setStartState() {",
            "currentToken = StringBuilder()",
            "currentState = State.START"
        ).determineAndCreateClosingBracketsExtension()
    }


    private fun generateCheckAndChangeStateFunction(): Sequence<String> {
        return sequenceOf(
            "private fun checkAndChangeState(char: Char, states: Map<Char, State>) {",
            "currentState = states[char] ?: State.IDENTIFIER",
            "currentToken.append(char)"
        ).determineAndCreateClosingBracketsExtension()
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
        return states.map { it.uppercase(Locale.getDefault()) }
    }

    private fun generateStateAndToken(): Sequence<String> {
        return sequenceOf(
            "private var currentState = State.START",
            "private var currentToken = StringBuilder()",
            EMPTY_LINE
        )
    }

    private fun generateStateMachine(): Sequence<String> {
        return (createStartStates() + createReadStates()).determineAndCreateClosingBracketsExtension()
    }

    private fun createStartStates(): Sequence<String> {
        val preCode = sequenceOf(
            "fun readStates(char: Char, tokens: MutableList<Token>) {",
            "when (currentState) {"
        )
        val startStateCases = createTopLevelStateCase()
        return preCode + startStateCases
    }

    private fun createReadStates(): Sequence<String> {
        return statesWithTokenType.asSequence().flatMap { (state, keyword) ->
            if (keyword != null) {
                createEndReadState(state)
            } else {
                val followUpStates = states.filter { it.startsWith(state) && it.length == state.length + 1 }
                val mapAssignment =
                    followUpStates.joinToString { "'${it.last()}' to State.${it.uppercase(Locale.getDefault())}" }

                sequenceOf(
                    "State.${state.uppercase(Locale.getDefault())} -> {",
                    "checkAndChangeState(char, mapOf($mapAssignment))"
                ).determineAndCreateClosingBracketsExtension()
            }
        }
    }

    private fun createEndReadState(state: String): Sequence<String> {
        return sequenceOf(
            "State.${state.uppercase(Locale.getDefault())} -> {",
            "if (char.isLetterOrDigit()) {",
            "currentToken.append(char)",
            "currentState = State.IDENTIFIER",
            "} else {",
            "tokens.add(Token(TokenType.${state.uppercase(Locale.getDefault())}, currentToken.toString()))",
            "setStartState()",
            "readStates(char, tokens)"
        ).determineAndCreateClosingBracketsExtension()
    }


    private fun createTopLevelStateCase(): Sequence<String> {
        val preCode = sequenceOf(
            "State.START -> {",
            "when (char) {"
        )

        val startStateCases = createStartStateCases().flatMap(StartStateCondition::getIfClauseAsSequence).asSequence()
        return (preCode + startStateCases).determineAndCreateClosingBracketsExtension()
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

    private fun Sequence<String>.determineAndCreateClosingBracketsExtension(attachSingleLine: Boolean = true): Sequence<String> =
        this + determineAndCreateClosingBrackets(this, attachSingleLine)
}

enum class NonSplittableStates(val condition: StateIfCondition?, val hasToBeLast: Boolean = false) {
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), true),
    SEMICOLON(CharIfCondition(';')),
    ASSIGN(CharIfCondition('=')),
    START(null);
}
