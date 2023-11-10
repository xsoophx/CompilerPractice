package cc.suffro.scannergenerator.generators

import cc.suffro.scannergenerator.data.NonSplittableState
import cc.suffro.scannergenerator.determineAndCreateClosingBracketsExtension
import java.util.*

class NonSplittableStateGenerator(private val literal: NonSplittableState) {
    fun generate(): Sequence<String> {
        val result = if (literal.hasCustomState) customMapping.getValue(literal) else generateDefaultState()
        return result.asSequence().determineAndCreateClosingBracketsExtension()
    }

    fun getCustomMapping(): List<String>? = customMapping[literal]

    private val customMapping: Map<NonSplittableState, List<String>> = mapOf(
        NonSplittableState.CHAR_LITERAL to listOf(
            state(),
            ifCondition(),
            addToken(),
            setStartState(),
            elseStatement(),
            throwException(IllegalArgumentException("Char literal must contain exactly one character!"))
        ),
        NonSplittableState.IDENTIFIER to listOf(
            state(),
            ifCondition(),
            appendToToken(),
            elseStatement(),
            addToken(),
            setStartState(),
            readStates()
        ),
        NonSplittableState.PLUS to listOf(
            checkAndChangeState()
        ),
        NonSplittableState.MINUS to listOf(
            checkAndChangeState()
        ),
    )

    private fun generateDefaultState(): List<String> {
        return listOf(
            state(),
            ifCondition(),
            appendToToken(),
            assignIdentifierToCurrentState(),
            elseStatement(),
            addToken(),
            setStartState(),
            readStates()
        )
    }

    private fun state(): String = "State.${literal.name.uppercase(Locale.getDefault())} -> {"
    private fun ifCondition(): String = "if (${literal.readStateCondition.toString()}) {"
    private fun appendToToken(): String = "currentToken.append(char)"
    private fun assignIdentifierToCurrentState(): String = "currentState = State.IDENTIFIER"
    private fun elseStatement(): String = "} else {"
    private fun addToken(): String =
        "tokens.add(Token(TokenType.${literal.name.uppercase(Locale.getDefault())}, currentToken.toString()))"

    private fun setStartState(): String = "setStartState()"
    private fun readStates(): String = "readStates(char, tokens)"
    private fun throwException(exception: Exception): String =
        "throw ${exception.javaClass.simpleName}(\"${exception.message}\")"

    private fun checkAndChangeState(): String =
        "checkAndChangeState(char, mapOf('${literal.startStateProperties?.condition}' to State.${literal.name.uppercase(Locale.getDefault())}), tokens)"

}