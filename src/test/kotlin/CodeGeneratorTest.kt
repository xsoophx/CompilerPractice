import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CodeGeneratorTest {

    @ParameterizedTest
    @MethodSource("statesByKeywords")
    fun `should create correct state enum class`(keywords: Array<KeywordState>, expected: String) {
        assertEquals(
            expected = expected,
            actual = CodeGenerator(keywords).generateStateEnumClass()
        )
    }

    companion object {
        @JvmStatic
        fun statesByKeywords() = listOf(
            Arguments.of(
                arrayOf(KeywordState.BOOL, KeywordState.DOUBLE, KeywordState.INT), """
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
                    IDENTIFIER
                }
                """.trimIndent()
            ),
            Arguments.of(
                arrayOf(KeywordState.BREAK, KeywordState.CONTINUE, KeywordState.ELSE), """
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
                    IDENTIFIER
                }
                """.trimIndent()
            ),
            Arguments.of(
                arrayOf(KeywordState.BOOL, KeywordState.BREAK), """
                enum class State {
                    B,
                    BO,
                    BOO,
                    BOOL,
                    BR,
                    BRE,
                    BREA,
                    BREAK,
                    IDENTIFIER
                }
                """.trimIndent()
            )
        )
    }
}