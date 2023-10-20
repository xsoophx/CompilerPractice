import cc.suffro.scannergenerator.EMPTY_LINE
import cc.suffro.scannergenerator.data.Generator
import cc.suffro.scannergenerator.data.TokenType
import cc.suffro.scannergenerator.determineAndCreateClosingBracketsExtension
import cc.suffro.scannergenerator.generators.StateEnumClassGenerator
import cc.suffro.scannergenerator.generators.StateMachineGenerator
import cc.suffro.scannergenerator.generators.TokenDataClassGenerator
import cc.suffro.scannergenerator.generators.TokenTypeEnumGenerator
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
        val codeLines = TokenTypeEnumGenerator.generate() +
                TokenDataClassGenerator.generate() +
                StateEnumClassGenerator(states).generate() +
                generateStateAndToken() +
                StateMachineGenerator(states).generate() +
                generateCheckAndChangeStateFunction() +
                generateSetStartStateFunction()

        return indent(codeLines)
    }

    private fun generateCheckAndChangeStateFunction(): Sequence<String> {
        return sequenceOf(
            "private fun $CHECK_AND_CHANGE_FUNCTION_NAME(char: Char, states: Map<Char, State>) {",
            "currentState = states[char] ?: State.IDENTIFIER",
            "if (currentState != State.IDENTIFIER || char in 'a'..'z' || char in 'A'..'Z') {",
            "currentToken.append(char)"
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
