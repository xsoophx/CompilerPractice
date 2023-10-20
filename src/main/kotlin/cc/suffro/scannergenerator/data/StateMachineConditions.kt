package cc.suffro.scannergenerator.data

import cc.suffro.scannergenerator.EMPTY_LINE

interface StateMachineCondition
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
