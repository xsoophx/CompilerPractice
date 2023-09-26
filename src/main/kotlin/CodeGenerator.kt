import HelperFunctions.addIndentation
import java.util.Locale

class CodeGenerator(private val keywords: Array<Keyword> = Keyword.values()) {

    private val states = keywords.flatMap(Keyword::splitKeywordToStates).distinct().sorted()

    fun generateStateEnumClass(): String {
        val codeLines = sequenceOf(
            0 to "enum class State {",
            0 to generateEnumEntries(),
            0 to "}"
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

    fun generateStateMachine(): String {
        val codeLines = sequenceOf(
            0 to "private var currentState = State.START",
            0 to "private var currentToken = StringBuilder()",
            0 to "fun readStates(char: Char, tokens: MutableList<Token>) {",
            4 to " when (currentState) {",
            8 to createStartStateCase(),
            0 to "}",
        )

        return codeLines.map { it.addIndentation() }.joinToString(separator = "\n")
    }

    fun createStartStateCase(): String {
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
        return startStateConditions.joinToString(separator = "\n")
    }
}

enum class NonSplittableStates(val condition: StateIfCondition?, val hasToBeLast: Boolean = false) {
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), true),
    SEMICOLON(CharIfCondition(';')),
    ASSIGN(CharIfCondition('=')),
    START(null);
}

