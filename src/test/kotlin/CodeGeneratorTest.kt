import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CodeGeneratorTest {

    @ParameterizedTest
    @MethodSource("statesByKeywords")
    fun `should create correct state enum class`(keywords: Array<Keyword>, expected: String) {
        assertEquals(
            expected = expected,
            actual = CodeGenerator(keywords).generateStateEnumClass()
        )
    }

    @Test
    fun `should create correct start state`() {
        val expected = listOf(
            StartStateCondition(CharIfCondition('i'), "I"),
            StartStateCondition(CharIfCondition(';'), "SEMICOLON"),
            StartStateCondition(CharIfCondition('='), "ASSIGN"),
            StartStateCondition(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), "IDENTIFIER"),
            StartStateCondition(StringIfCondition("in '0'..'9'"), "INT_LITERAL"),
        )

        assertEquals(expected = expected, actual = CodeGenerator(arrayOf(Keyword.INT)).createStartStateCase())
    }

    companion object {
        @JvmStatic
        fun statesByKeywords() = listOf(
            Arguments.of(
                arrayOf(Keyword.BOOL, Keyword.DOUBLE, Keyword.INT), """
                enum class State {
                    B,
                    BO,
                    BOO,
                    BOOL,
                    D,
                    DO,
                    DOU,
                    DOUB,
                    DOUBL,
                    DOUBLE,
                    I,
                    IN,
                    INT,
                    ASSIGN,
                    IDENTIFIER,
                    INT_LITERAL,
                    SEMICOLON,
                    START
                }
                """.trimIndent()
            ),
            Arguments.of(
                arrayOf(Keyword.BREAK, Keyword.CONTINUE, Keyword.ELSE), """
                enum class State {
                    B,
                    BR,
                    BRE,
                    BREA,
                    BREAK,
                    C,
                    CO,
                    CON,
                    CONT,
                    CONTI,
                    CONTIN,
                    CONTINU,
                    CONTINUE,
                    E,
                    EL,
                    ELS,
                    ELSE,
                    ASSIGN,
                    IDENTIFIER,
                    INT_LITERAL,
                    SEMICOLON,
                    START
                }
                """.trimIndent()
            ),
            Arguments.of(
                arrayOf(Keyword.BOOL, Keyword.BREAK), """
                enum class State {
                    B,
                    BO,
                    BOO,
                    BOOL,
                    BR,
                    BRE,
                    BREA,
                    BREAK,
                    ASSIGN,
                    IDENTIFIER,
                    INT_LITERAL,
                    SEMICOLON,
                    START
                }
                """.trimIndent()
            )
        )
    }
}