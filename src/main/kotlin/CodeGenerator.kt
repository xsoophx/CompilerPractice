import java.util.Locale
import kotlin.math.max

const val CHECK_AND_CHANGE_FUNCTION_NAME = "checkAndChangeState"
const val STANDARD_INDENTATION = 4

class CodeGenerator(
    keywords: Sequence<TokenType> = TokenType.tokenTypeKeywords,
) {

    private val states =
        keywords
            .flatMap { keyword -> TokenType.splitKeywordToStates(keyword) }
            .distinctBy { it.second }
            .toList()

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
            "private fun $CHECK_AND_CHANGE_FUNCTION_NAME(char: Char, states: Map<Char, State>) {",
            "currentState = states[char] ?: State.IDENTIFIER",
            "if (currentState != State.IDENTIFIER || char in 'a'..'z' || char in 'A'..'Z') {",
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
        val uppercaseKeywords =
            TokenType.values().sortedBy { it.clazz }.map { it.name.uppercase(Locale.getDefault()) }
        val code = sequenceOf("enum class TokenType {") + uppercaseKeywords.map { "$it," }

        return code + determineAndCreateClosingBrackets(code)

    }

    fun generateStateEnumClass(): Sequence<String> {
        val code = sequenceOf("enum class State {") + generateEnumEntries()
        return code + determineAndCreateClosingBrackets(code)
    }

    private fun generateEnumEntries(): List<String> {
        val states = getKeywordStatesAsString() + NonSplittableStates.values()
            .map { it.name.uppercase(Locale.getDefault()) }.sorted()

        return states.map { "$it," }
    }

    private fun getKeywordStatesAsString(): List<String> {
        return states.map { it.second.uppercase(Locale.getDefault()) }
    }

    private fun generateStateAndToken(): Sequence<String> {
        return sequenceOf(
            "private var currentState = State.START",
            "private var currentToken = StringBuilder()",
            EMPTY_LINE
        )
    }

    private fun generateStateMachine(): Sequence<String> {
        return (createStartStates() + createReadStates() + createSymbolStates() + createLiteralStates() + createIdentifierState()).determineAndCreateClosingBracketsExtension()
    }

    private fun createStartStates(): Sequence<String> {
        val preCode = sequenceOf(
            "fun readStates(char: Char, tokens: MutableList<Token>) {",
            "when (currentState) {"
        )
        val startStateCases = createTopLevelStateCase()
        return preCode + startStateCases
    }

    private fun splitStatesWithSpecificFollowState(): Sequence<String> {
        val statesWithSpecificFollowState =
            TokenType.tokenTypeKeywords.filter { it.omitCharInformation != null && it.omitCharInformation.omitLastCharInState }
        val splitStates = statesWithSpecificFollowState.flatMap { TokenType.splitKeywordToStates(it) }.toList()

        val result = splitStates.flatMap { (tokenType, state) ->
            val followUpStates = states.filter { it.second.startsWith(state) && it.second.length == state.length + 1 }

            if (followUpStates.isEmpty()) {
                val mapAssignment =
                    "'${tokenType.name.last().lowercase()}' to State.${tokenType.omitCharInformation?.nextTokenType}"

                createStateWithMap(state, mapAssignment)

            } else {
                val mapAssignment = createMapAssignment(followUpStates)
                createStateWithMap(state, mapAssignment)
            }
        }

        return result.asSequence()

    }

    private fun createReadStates(): Sequence<String> {
        val statesWithSpecificFollowState = splitStatesWithSpecificFollowState()
        val readStates = states
            .filter { it.first.omitCharInformation == null }
            .asSequence()
            .flatMap { (tokenType, state) ->
                if (tokenType.name.length == state.length) {
                    createEndReadState(state)
                } else {
                    val followUpStates =
                        states.filter { it.second.startsWith(state) && it.second.length == state.length + 1 }
                    val mapAssignment = createMapAssignment(followUpStates)
                    createStateWithMap(state, mapAssignment)

                }
            }

        return readStates + statesWithSpecificFollowState
    }

    private fun createMapAssignment(followUpStates: List<Pair<TokenType, String>>): String {
        return followUpStates.values().joinToString {
            "'${it.last().lowercase()}' to State.${it.uppercase(Locale.getDefault())}"
        }
    }

    private fun createStateWithMap(state: String, mapAssignment: String): Sequence<String> {
        return sequenceOf(
            "State.${state.uppercase(Locale.getDefault())} -> {",
            "$CHECK_AND_CHANGE_FUNCTION_NAME(char, mapOf($mapAssignment))"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun List<Pair<TokenType, String>>.values() = map { it.second }

    private fun createSymbolStates(): Sequence<String> {
        val symbols = TokenType.values().filter { it.clazz == TokenClass.SYMBOL }.map { it.name }

        return symbols.asSequence().flatMap { symbol ->
            sequenceOf(
                "State.${symbol.uppercase(Locale.getDefault())} -> {",
                "tokens.add(Token(TokenType.${symbol.uppercase(Locale.getDefault())}, currentToken.toString()))",
                "setStartState()",
                "readStates(char, tokens)"
            ).determineAndCreateClosingBracketsExtension()
        }
    }

    private fun createIdentifierState(): Sequence<String> {
        return sequenceOf(
            "State.IDENTIFIER -> {",
            "if (char.isLetterOrDigit()) {",
            "currentToken.append(char)",
            "} else {",
            "tokens.add(Token(TokenType.IDENTIFIER, currentToken.toString()))",
            "setStartState()",
            "readStates(char, tokens)"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun createLiteralStates(): Sequence<String> {
        val literals = TokenType.values().filter { it.clazz == TokenClass.LITERAL }.map { it.name }
        val nonSplittableStates = literals.mapNotNull { NonSplittableStates.byName(it) }

        return nonSplittableStates.asSequence().flatMap { literal ->
            sequenceOf(
                "State.${literal.name.uppercase(Locale.getDefault())} -> {",
                "if (${literal.readStateCondition.toString()}) {",
                "currentToken.append(char)",
                "currentState = State.IDENTIFIER",
                "} else {",
                "tokens.add(Token(TokenType.${literal.name.uppercase(Locale.getDefault())}, currentToken.toString()))",
                "setStartState()",
                "readStates(char, tokens)"
            ).determineAndCreateClosingBracketsExtension()
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
            states.map { it.second }.filter { it.length == 1 }
                .sorted()
                .map {
                    StartStateCondition(
                        condition = CharIfCondition(it.first()),
                        nextState = it.uppercase(Locale.getDefault())
                    )
                }

        val nonSplittableStates = NonSplittableStates.values()
            .asSequence()
            .filter { it.startStateCondition != null }
            .sortedBy { it.hasToBeLast }
            .map {
                StartStateCondition(
                    condition = it.startStateCondition!!,
                    nextState = it.name.uppercase(Locale.getDefault()),
                    appendChar = it.appendChar,
                )
            }

        return (singleCharStates + nonSplittableStates).toList()
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
        val reducedLine = trimmedLine.filterForQuotes("\"").filterForQuotes("'")
        return indentation + (reducedLine.count { "{([".contains(it) } - reducedLine.count { "}])".contains(it) }) * STANDARD_INDENTATION
    }

    private fun String.filterForQuotes(delimiter: String): String {
        return split(delimiter).filter { it.lastOrNull() != '\\' }.filterIndexed { index, _ -> index % 2 == 0 }
            .joinToString("")
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

// add more literals
enum class NonSplittableStates(
    val startStateCondition: StateIfCondition? = null,
    val readStateCondition: StateIfCondition? = null,
    val hasToBeLast: Boolean = false,
    val appendChar: Boolean = true
) {
    APOSTROPHE(StringIfCondition("'\\''"), appendChar = false),
    ASSIGN(CharIfCondition('=')),
    CLOSING_BRACKET(CharIfCondition(')')),
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), hasToBeLast = true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), StringIfCondition("char.isDigit()"), true),
    OPENING_BRACKET(CharIfCondition('(')),
    SEMICOLON(CharIfCondition(';')),
    START(null);

    companion object {
        fun byName(name: String): NonSplittableStates? {
            return values().find { it.name == name }
        }
    }
}
