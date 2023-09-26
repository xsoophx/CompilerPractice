import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KeywordStateTest {

    @ParameterizedTest
    @MethodSource("keywords")
    fun `should split keywords in correct states`(keywordState: Keyword, expected: List<String>) {
        val splits = keywordState.splitKeywordToStates()
        assertEquals(expected = expected, actual = splits.toList())
    }

    companion object {
        @JvmStatic
        fun keywords() = listOf(
            Arguments.of(Keyword.BOOL, listOf("b", "bo", "boo", "bool")),
            Arguments.of(Keyword.DOUBLE, listOf("d", "do", "dou", "doub", "doubl", "double")),
            Arguments.of(Keyword.INT, listOf("i", "in", "int")),
            Arguments.of(Keyword.BREAK, listOf("b", "br", "bre", "brea", "break")),
            Arguments.of(Keyword.CONTINUE, listOf("c", "co", "con", "cont", "conti", "contin", "continu", "continue")),
            Arguments.of(Keyword.ELSE, listOf("e", "el", "els", "else")),
            Arguments.of(Keyword.FOR, listOf("f", "fo", "for")),
            Arguments.of(Keyword.IF, listOf("i", "if")),
            Arguments.of(Keyword.RETURN, listOf("r", "re", "ret", "retu", "retur", "return")),
            Arguments.of(Keyword.WHILE, listOf("w", "wh", "whi", "whil", "while")),
            Arguments.of(Keyword.VOID, listOf("v", "vo", "voi", "void")),
            Arguments.of(Keyword.DO, listOf("d", "do")),
            Arguments.of(Keyword.FLOAT, listOf("f", "fl", "flo", "floa", "float")),
            Arguments.of(Keyword.LONG, listOf("l", "lo", "lon", "long")),
            Arguments.of(Keyword.SHORT, listOf("s", "sh", "sho", "shor", "short")),
        )
    }
}