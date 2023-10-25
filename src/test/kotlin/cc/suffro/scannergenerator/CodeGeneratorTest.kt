package cc.suffro.scannergenerator

import cc.suffro.scannergenerator.generators.CodeGenerator
import cc.suffro.scannergenerator.data.CharIfCondition
import cc.suffro.scannergenerator.data.StartStateCondition
import cc.suffro.scannergenerator.data.StringIfCondition
import cc.suffro.scannergenerator.data.TokenType
import cc.suffro.scannergenerator.generators.StateEnumClassGenerator
import cc.suffro.scannergenerator.generators.StateMachineGenerator
import kotlin.IllegalArgumentException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

// TODO: fix this test
class CodeGeneratorTest {

    @ParameterizedTest
    @MethodSource("statesByKeywords")
    fun `should create correct state enum class`(keywords: Sequence<TokenType>, expected: String) {
        val codeGenerator = CodeGenerator(keywords)
        val enumClass = StateEnumClassGenerator(codeGenerator.states).generate()

        assertEquals(
            expected = expected,
            actual = indent(enumClass)
        )
    }

    @Test
    fun `should create correct start states`() {
        val expected = listOf(
            StartStateCondition(CharIfCondition('i'), "I"),
            StartStateCondition(CharIfCondition(';'), "SEMICOLON"),
            StartStateCondition(CharIfCondition('='), "ASSIGN"),
            StartStateCondition(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), "IDENTIFIER"),
            StartStateCondition(StringIfCondition("in '0'..'9'"), "INT_LITERAL"),
        )

        val keywords = CodeGenerator(sequenceOf(TokenType.INT)).states
        assertEquals(expected = expected, actual = StateMachineGenerator(keywords).createStartStateCases())
    }

    @Test
    fun `should create correct code`() {
        val code = CodeGenerator().generate()
        assertContains(code, "xState.START")
    }

    companion object {
        @JvmStatic
        fun statesByKeywords() = listOf(
            Arguments.of(
                arrayOf(TokenType.BOOL, TokenType.DOUBLE, TokenType.INT), """
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
                    START,
                }
                
                """.trimIndent()
            ),
            Arguments.of(
                arrayOf(TokenType.BREAK, TokenType.CONTINUE, TokenType.ELSE), """
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
                    START,
                }
                
                """.trimIndent()
            ),
            Arguments.of(
                arrayOf(TokenType.BOOL, TokenType.BREAK), """
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
                    START,
                }
                
                """.trimIndent()
            )
        )
    }
}