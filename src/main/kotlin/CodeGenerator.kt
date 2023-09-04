import HelperFunctions.addIndentation
import java.util.Locale

class CodeGenerator(private val keywords: Array<KeywordState> = KeywordState.values()) {
    fun generateStateEnumClass(): String {

        val codeLines = sequenceOf(
            0 to "enum class State {",
            4 to "${getKeywordStatesAsString()},",
            4 to "IDENTIFIER",
            0 to "}"
        )
        return codeLines.map { it.addIndentation() }.joinToString(separator = "\n")
    }

    private fun getKeywordStatesAsString(): String {
        val states = keywords.flatMap(KeywordState::splitKeywordToStates).toSet()
        return states.joinToString(separator = ",\n    ") { it.uppercase(Locale.getDefault()) }
    }

}

