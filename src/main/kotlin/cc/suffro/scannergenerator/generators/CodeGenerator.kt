package cc.suffro.scannergenerator.generators

import cc.suffro.scannergenerator.EMPTY_LINE
import cc.suffro.scannergenerator.data.Generator
import cc.suffro.scannergenerator.data.TokenType
import cc.suffro.scannergenerator.determineAndCreateClosingBracketsExtension
import cc.suffro.scannergenerator.indent

const val CHECK_AND_CHANGE_FUNCTION_NAME = "checkAndChangeState"
const val STANDARD_INDENTATION = 4

class CodeGenerator(keywords: Sequence<TokenType> = TokenType.tokenTypeKeywords) : Generator {

    val states =
        keywords
            .flatMap { keyword -> TokenType.splitKeywordToStates(keyword) }
            .distinctBy { it.second }
            .toList()

    fun generate(): String {
        val codeLines =
            generateImports() +
                    TokenTypeEnumGenerator.generate() +
                    TokenDataClassGenerator.generate() +
                    StateEnumClassGenerator(states).generate() +
                    generateStateAndToken() +
                    StateMachineGenerator(states).generate() +
                    generateCheckAndChangeStateFunction() +
                    generateTokenTypeByStateFunction() +
                    generateSetStartStateFunction()

        return indent(codeLines)
    }

    private fun generateImports(): Sequence<String> {
        return sequenceOf("import java.util.*")
    }

    private fun generateCheckAndChangeStateFunction(): Sequence<String> {
        return sequenceOf(
            "private fun $CHECK_AND_CHANGE_FUNCTION_NAME(char: Char, states: Map<Char, State>, tokens: MutableList<Token>) {",
            "currentState = states[char] ?: State.IDENTIFIER",
            "if (currentState != State.IDENTIFIER || char in 'a'..'z' || char in 'A'..'Z') {",
            "currentToken.append(char)",
            "} else {",
            "val tokenType = tokenTypeByState(currentState)",
            "tokens.add(Token(tokenType, currentToken.toString()))",
            "setStartState()",
            "readStates(char, tokens)"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun generateTokenTypeByStateFunction(): Sequence<String> {
        return sequenceOf(
            "private fun tokenTypeByState(state: State): TokenType {",
            "return TokenType.values()",
            ".find { it.name.uppercase(Locale.getDefault()) == state.name.uppercase(Locale.getDefault()) }",
            "?: throw IllegalArgumentException(\"Unknown state: \$state\")"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun generateSetStartStateFunction(): Sequence<String> {
        return sequenceOf(
            "private fun setStartState() {",
            "currentToken = StringBuilder()",
            "currentState = State.START"
        ).determineAndCreateClosingBracketsExtension()
    }

    private fun generateStateAndToken(): Sequence<String> {
        return sequenceOf(
            "private var currentState = State.START",
            "private var currentToken = StringBuilder()",
            EMPTY_LINE
        )
    }
}
