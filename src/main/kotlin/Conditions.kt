import HelperFunctions.addIndentation

data class StartStateCondition(val condition: StateIfCondition, val nextState: String) : StateMachineCondition {
    override fun toString(): String {
        val ifClause = when (condition) {
            is CharIfCondition -> "'${condition.char}'"
            is StringIfCondition -> condition.string
            else -> throw IllegalArgumentException("Unknown condition type")
        }
        return "$ifClause -> {\n" +
                "    currentState = State.$nextState\n" +
                "    currentToken.append(char)\n" +
                "}\n"
    }
}

interface StateIfCondition

data class CharIfCondition(val char: Char) : StateIfCondition
data class StringIfCondition(val string: String) : StateIfCondition

interface StateMachineCondition
data class StateMachineState(
    val state: String,
    val conditions: List<StateMachineCondition>
)

data class StateCondition(val currentState: String, val possibleFollowStates: Map<Char, String>) :
    StateMachineCondition {
    override fun toString(): String {
        val stateCases = sequenceOf(
            0 to "State.$currentState -> {",
            4 to "$CHECK_AND_CHANGE_FUNCTION_NAME(char, mapOf(${createStateMap()}))",
            0 to "}"
        )

        return stateCases.map { it.addIndentation() }.joinToString(separator = "\n")
    }

    private fun createStateMap(): String {
       return possibleFollowStates.map { (char, state) ->
                "'$char' to State.$state"
            }.joinToString(separator = ", ")
    }
}