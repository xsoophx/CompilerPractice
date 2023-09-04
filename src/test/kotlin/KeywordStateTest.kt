import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class KeywordStateTest {

    @ParameterizedTest
    @MethodSource("keywords")
    fun `should split keywords in correct states`(keywordState: KeywordState, expected: List<String>) {
        val splits = keywordState.splitKeywordToStates()
        assertEquals(expected = expected, actual = splits)
    }

    companion object {
        @JvmStatic
        fun keywords() = listOf(
            Arguments.of(KeywordState.BOOL, listOf("b", "bo", "boo", "bool")),
            Arguments.of(KeywordState.DOUBLE, listOf("d", "do", "dou", "doub", "doubl", "double")),
            Arguments.of(KeywordState.INT, listOf("i", "in", "int")),
            Arguments.of(KeywordState.BREAK, listOf("b", "br", "bre", "brea", "break")),
            Arguments.of(KeywordState.CONTINUE, listOf("c", "co", "con", "cont", "conti", "contin", "continu", "continue")),
            Arguments.of(KeywordState.ELSE, listOf("e", "el", "els", "else")),
            Arguments.of(KeywordState.FOR, listOf("f", "fo", "for")),
            Arguments.of(KeywordState.IF, listOf("i", "if")),
            Arguments.of(KeywordState.RETURN, listOf("r", "re", "ret", "retu", "retur", "return")),
            Arguments.of(KeywordState.WHILE, listOf("w", "wh", "whi", "whil", "while")),
            Arguments.of(KeywordState.VOID, listOf("v", "vo", "voi", "void")),
            Arguments.of(KeywordState.DO, listOf("d", "do")),
            Arguments.of(KeywordState.FLOAT, listOf("f", "fl", "flo", "floa", "float")),
            Arguments.of(KeywordState.LONG, listOf("l", "lo", "lon", "long")),
            Arguments.of(KeywordState.SHORT, listOf("s", "sh", "sho", "shor", "short")),
        )
    }
}