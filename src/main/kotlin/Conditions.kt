data class StartStateCondition(val condition: StateIfCondition, val nextState: String, val appendChar: Boolean = true) :
    StateMachineCondition {
    private fun getIfClause(): String {
        val ifClause = when (condition) {
            is CharIfCondition -> "'${condition.char.lowercase()}'"
            is StringIfCondition -> condition.string
            else -> throw IllegalArgumentException("Unknown condition type")
        }
        return "$ifClause -> {"
    }

    fun getIfClauseAsSequence(): Sequence<String> {
        return sequenceOf(
            getIfClause(),
            "currentState = State.$nextState",
            "currentToken.append(char)".takeIf { appendChar },
            "}",
            EMPTY_LINE
        ).filterNotNull()
    }

    override fun toString(): String {
        return getIfClauseAsSequence().joinToString("\n")
    }
}

interface StateIfCondition

data class CharIfCondition(val char: Char) : StateIfCondition {
    override fun toString(): String {
        return char.toString()
    }
}

data class StringIfCondition(val string: String) : StateIfCondition {
    override fun toString(): String {
        return string
    }
}

interface StateMachineCondition