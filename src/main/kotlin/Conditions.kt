data class StartStateCondition(val condition: StateIfCondition, val state: String) {
    override fun toString(): String {
        val ifClause = when (condition) {
            is CharIfCondition -> "'${condition.char}'"
            is StringIfCondition -> condition.string
            else -> throw IllegalArgumentException("Unknown condition type")
        }
        return "$ifClause -> {\n" +
                "    currentState = State.$state\n" +
                "    currentToken.append(char)\n" +
                "}\n"
    }
}

interface StateIfCondition

data class CharIfCondition(val char: Char) : StateIfCondition
data class StringIfCondition(val string: String) : StateIfCondition
