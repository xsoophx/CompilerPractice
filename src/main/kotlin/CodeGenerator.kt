import java.util.Locale

const val CHECK_AND_CHANGE_FUNCTION_NAME = "checkAndChangeState"
const val STANDARD_INDENTATION = 4

class CodeGenerator(keywords: Array<Keyword> = Keyword.values()) {

    private val states = keywords.flatMap(Keyword::splitKeywordToStates).distinct().sorted()

    fun generate(): String {
        val codeLines = sequenceOf(
            0 to generateStateEnumClass(),
            0 to generateStateAndToken(),
            0 to generateStateMachine()
        )
        return codeLines.map { it.addIndentation() }.joinToString(separator = "\n")
    }

    fun generateStateEnumClass(): String {
        val codeLines = sequenceOf(
            0 to "enum class State {",
            0 to generateEnumEntries(),
            0 to "}\n"
        )
        return codeLines.map { it.addIndentation() }.joinToString(separator = "\n")
    }

    private fun generateEnumEntries(): String {
        val entries =
            sequenceOf(4 to getKeywordStatesAsString()) + NonSplittableStates.values()
                .map { 4 to it.name.uppercase(Locale.getDefault()) }.sortedBy { it.second }

        return entries.map { it.addIndentation() }.joinToString(separator = ",\n")
    }


    private fun getKeywordStatesAsString(): String {
        return states.joinToString(separator = ",\n    ") { it.uppercase(Locale.getDefault()) }
    }

    private fun generateStateAndToken(): String {
        return sequenceOf(
            0 to "private var currentState = State.START",
            0 to "private var currentToken = StringBuilder()\n"
        ).map { it.addIndentation() }.joinToString(separator = "\n")
    }

    fun generateStateMachine(baseIndentation: Int = 0): String {
        val preCode = sequenceOf(
            "fun readStates(char: Char, tokens: MutableList<Token>) {",
            "when (currentState)"
        )
        val code = createStartStateCase().map(StartStateCondition::getClauseAsCodeBlock).asSequence()

        val codeLines = Codeblock(preCode, code)
        return codeLines.asString(indentation = baseIndentation)
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

}

enum class NonSplittableStates(val condition: StateIfCondition?, val hasToBeLast: Boolean = false) {
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), true),
    SEMICOLON(CharIfCondition(';')),
    ASSIGN(CharIfCondition('=')),
    START(null);
}
