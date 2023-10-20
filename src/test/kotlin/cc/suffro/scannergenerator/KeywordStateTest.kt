package cc.suffro.scannergenerator

import cc.suffro.scannergenerator.data.TokenType
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KeywordStateTest {

    @ParameterizedTest
    @MethodSource("keywords")
    fun `should split keywords in correct states`(keyword: TokenType, expected: List<String>) {
        val splits = TokenType.splitKeywordToStates(keyword)
        assertEquals(expected = expected.map { keyword to it }, actual = splits.toList())
    }

    companion object {
        @JvmStatic
        fun keywords() = listOf(
            Arguments.of(TokenType.BOOL, listOf("B", "BO", "BOO", "BOOL")),
            Arguments.of(TokenType.DOUBLE, listOf("D", "DO", "DOU", "DOUB", "DOUBL", "DOUBLE")),
            Arguments.of(TokenType.INT, listOf("I", "IN", "INT")),
            Arguments.of(TokenType.BREAK, listOf("B", "BR", "BRE", "BREA", "BREAK")),
            Arguments.of(
                TokenType.CONTINUE,
                listOf("C", "CO", "CON", "CONT", "CONTI", "CONTIN", "CONTINU", "CONTINUE")
            ),
            Arguments.of(TokenType.ELSE, listOf("E", "EL", "ELS", "ELSE")),
            Arguments.of(TokenType.FOR, listOf("F", "FO", "FOR")),
            Arguments.of(TokenType.IF, listOf("I", "IF")),
            Arguments.of(TokenType.RETURN, listOf("R", "RE", "RET", "RETU", "RETUR", "RETURN")),
            Arguments.of(TokenType.WHILE, listOf("W", "WH", "WHI", "WHIL", "WHILE")),
            Arguments.of(TokenType.VOID, listOf("V", "VO", "VOI", "VOID")),
            Arguments.of(TokenType.DO, listOf("D", "DO")),
            Arguments.of(TokenType.FLOAT, listOf("F", "FL", "FLO", "FLOA", "FLOAT")),
            Arguments.of(TokenType.LONG, listOf("L", "LO", "LON", "LONG")),
            Arguments.of(TokenType.SHORT, listOf("S", "SH", "SHO", "SHOR", "SHORT")),
        )
    }
}