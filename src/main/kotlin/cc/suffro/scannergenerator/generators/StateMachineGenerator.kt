package cc.suffro.scannergenerator.generators

import CHECK_AND_CHANGE_FUNCTION_NAME
import cc.suffro.scannergenerator.data.CharIfCondition
import cc.suffro.scannergenerator.data.Generator
import cc.suffro.scannergenerator.data.NonSplittableState
import cc.suffro.scannergenerator.data.StartStateCondition
import cc.suffro.scannergenerator.data.TokenClass
import cc.suffro.scannergenerator.data.TokenType
import cc.suffro.scannergenerator.determineAndCreateClosingBracketsExtension
import cc.suffro.scannergenerator.values
import java.util.*

class StateMachineGenerator(private val states: List<Pair<TokenType, String>>) : Generator {

    fun generate(): Sequence<String> {
        return (createStartStates() +
                createReadStates() +
                createSymbolStates() +
                createLiteralStates() +
                createIdentifierState()
                ).determineAndCreateClosingBracketsExtension()
    }

    private fun createStartStates(): Sequence<String> {
        val preCode = sequenceOf(
            "fun readStates(char: Char, tokens: MutableList<Token>) {",
            "when (currentState) {"
        )
        val startStateCases = createTopLevelStateCase()
        return preCode + startStateCases
    }

    private fun createReadStates(): Sequence<String> {
        val statesWithSpecificFollowState = splitStatesWithSpecificFollowState()
        val readStates = states
            .filter { it.first.omitCharInformation == null }
            .asSequence()
            .flatMap { (tokenType, state) ->
                if (tokenType.name.length == state.length) {
                    createEndReadState(state)
                } else {
                    val followUpStates =
                        states.filter { it.second.startsWith(state) && it.second.length == state.length + 1 }
                    val mapAssignment = createMapAssignment(followUpStates)
                    createStateWithMap(state, mapAssignment)

                }
            }

        return readStates + statesWithSpecificFollowState
    }

    private fun createMapAssignment(followUpStates: List<Pair<TokenType, String>>): String {
        return followUpStates.values().joinToString {
            "'${it.last().lowercase()}' to State.${it.uppercase(Locale.getDefault())}"
        }
    }

    private fun createStateWithMap(state: String, mapAssignment: String): Sequence<String> {
        return sequenceOf(
            "State.${state.uppercase(Locale.getDefault())} -> {",
            "$CHECK_AND_CHANGE_FUNCTION_NAME(char, mapOf($mapAssignment))"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun splitStatesWithSpecificFollowState(): Sequence<String> {
        val statesWithSpecificFollowState =
            TokenType.tokenTypeKeywords.filter { it.omitCharInformation != null && it.omitCharInformation.omitLastCharInState }
        val splitStates = statesWithSpecificFollowState.flatMap { TokenType.splitKeywordToStates(it) }.toList()

        val result = splitStates.flatMap { (tokenType, state) ->
            val followUpStates = states.filter { it.second.startsWith(state) && it.second.length == state.length + 1 }

            if (followUpStates.isEmpty()) {
                val mapAssignment =
                    "'${tokenType.name.last().lowercase()}' to State.${tokenType.omitCharInformation?.nextTokenType}"

                createStateWithMap(state, mapAssignment)

            } else {
                val mapAssignment = createMapAssignment(followUpStates)
                createStateWithMap(state, mapAssignment)
            }
        }

        return result.asSequence()

    }

    private fun createSymbolStates(): Sequence<String> {
        val symbols = TokenType.values().filter { it.clazz == TokenClass.SYMBOL }.map { it.name }

        return symbols.asSequence().flatMap { symbol ->
            sequenceOf(
                "State.${symbol.uppercase(Locale.getDefault())} -> {",
                "tokens.add(Token(TokenType.${symbol.uppercase(Locale.getDefault())}, currentToken.toString()))",
                "setStartState()",
                "readStates(char, tokens)"
            ).determineAndCreateClosingBracketsExtension()
        }
    }

    private fun createLiteralStates(): Sequence<String> {
        val literals = TokenType.values().filter { it.clazz == TokenClass.LITERAL }.map { it.name }
        val nonSplittableStates = literals.mapNotNull { NonSplittableState.byName(it) }

        return nonSplittableStates.asSequence().flatMap { literal ->
            sequenceOf(
                "State.${literal.name.uppercase(Locale.getDefault())} -> {",
                "if (${literal.readStateCondition.toString()}) {",
                "currentToken.append(char)",
                "currentState = State.IDENTIFIER",
                "} else {",
                "tokens.add(Token(TokenType.${literal.name.uppercase(Locale.getDefault())}, currentToken.toString()))",
                "setStartState()",
                "readStates(char, tokens)"
            ).determineAndCreateClosingBracketsExtension()
        }
    }

    private fun createIdentifierState(): Sequence<String> {
        return sequenceOf(
            "State.IDENTIFIER -> {",
            "if (char.isLetterOrDigit()) {",
            "currentToken.append(char)",
            "} else {",
            "tokens.add(Token(TokenType.IDENTIFIER, currentToken.toString()))",
            "setStartState()",
            "readStates(char, tokens)"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun createTopLevelStateCase(): Sequence<String> {
        val preCode = sequenceOf(
            "State.START -> {",
            "when (char) {"
        )

        val startStateCases = createStartStateCases().flatMap(StartStateCondition::getIfClauseAsSequence).asSequence()
        return (preCode + startStateCases).determineAndCreateClosingBracketsExtension()
    }

    fun createStartStateCases(): List<StartStateCondition> {
        val singleCharStates =
            states.map { it.second }.filter { it.length == 1 }
                .sorted()
                .map {
                    StartStateCondition(
                        condition = CharIfCondition(it.first()),
                        nextState = it.uppercase(Locale.getDefault())
                    )
                }

        val nonSplittableStates = NonSplittableState.values()
            .asSequence()
            .filter { it.startStateCondition != null }
            .sortedBy { it.hasToBeLast }
            .map {
                StartStateCondition(
                    condition = it.startStateCondition!!,
                    nextState = it.name.uppercase(Locale.getDefault()),
                    appendChar = it.appendChar,
                )
            }

        return (singleCharStates + nonSplittableStates).toList()
    }

    private fun createEndReadState(state: String): Sequence<String> {
        return sequenceOf(
            "State.${state.uppercase(Locale.getDefault())} -> {",
            "if (char.isLetterOrDigit()) {",
            "currentToken.append(char)",
            "currentState = State.IDENTIFIER",
            "} else {",
            "tokens.add(Token(TokenType.${state.uppercase(Locale.getDefault())}, currentToken.toString()))",
            "setStartState()",
            "readStates(char, tokens)"
        ).determineAndCreateClosingBracketsExtension()
    }
}