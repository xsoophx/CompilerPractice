import java.util.Locale

class CodeGenerator(private val keywords: Array<KeywordState> = KeywordState.values()) {
    fun generateEnumClass(): String {
        val states = keywords.flatMap(KeywordState::splitKeywordToStates).toSet()
        return "enum class State {\n" +
                "    ${states.joinToString(separator = ",\n    ") { it.uppercase(Locale.getDefault()) }}\n" +
                "}"
    }
}

