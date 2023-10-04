import java.util.Locale

const val CHECK_AND_CHANGE_FUNCTION_NAME = "checkAndChangeState"
const val STANDARD_INDENTATION = 4

class CodeGenerator(keywords: Array<Keyword> = Keyword.values()) {

    private val states = keywords.flatMap(Keyword::splitKeywordToStates).distinct().sorted()

    fun generate(): String {
        val codeLines = generateStateEnumClass() +
                generateStateAndToken() +
                generateStateMachine() +
                sequenceOf("")

        return indent(codeLines)
    }

    fun generateStateEnumClass(): Sequence<String> {
        return sequenceOf("enum class State {") + generateEnumEntries() + sequenceOf("}", EMPTY_LINE)
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

    fun generateStateMachine(): Sequence<String> {
        val preCode = sequenceOf(
            "fun readStates(char: Char, tokens: MutableList<Token>) {",
            "when (currentState) {"
        )
        val code = createStartStateCase().map(StartStateCondition::toString).asSequence()

        return preCode + code + sequenceOf("}", "}")
    }

    fun createStartStateCase(): List<StartStateCondition> {
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
            val reducedLine = line.trim()
            if (reducedLine.isNotEmpty()) {
                if ("}])".contains(reducedLine.first())) {
                    codeLines[lineNumber] =
                        " ".repeat((indentation - STANDARD_INDENTATION).coerceAtLeast(0)) + reducedLine
                } else {
                    codeLines[lineNumber] = " ".repeat(indentation.coerceAtLeast(0)) + reducedLine
                }

                reducedLine.forEach {

                    if ("{([".contains(it)) {
                        indentation += STANDARD_INDENTATION
                    }
                    if ("}])".contains(it)) {
                        indentation -= STANDARD_INDENTATION
                    }
                }
            } else {
                codeLines[lineNumber] = EMPTY_LINE
            }
        }

        return codeLines.joinToString(separator = "\n")
    }
}

enum class NonSplittableStates(val condition: StateIfCondition?, val hasToBeLast: Boolean = false) {
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), true),
    SEMICOLON(CharIfCondition(';')),
    ASSIGN(CharIfCondition('=')),
    START(null);
}
