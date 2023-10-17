import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class LexerTest {

    @ParameterizedTest
    @MethodSource("getIntAssignments")
    fun `should detect correct tokens for int assign`(assignment: String, tokenTypes: List<Token>) {
        val tokens = Lexer().tokenize(assignment)
        assertEquals(expected = tokenTypes, actual = tokens)
    }

    companion object {
        private val tokenTypesOfA = listOf(
            Token(TokenType.INT, "int"),
            Token(TokenType.IDENTIFIER, "a"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.INT_LITERAL, "1"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val tokenTypesOfB = listOf(
            Token(TokenType.INT, "int"),
            Token(TokenType.IDENTIFIER, "b"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.INT_LITERAL, "3"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val booleanTokenTypes = listOf(
            Token(TokenType.BOOL, "bool"),
            Token(TokenType.IDENTIFIER, "b"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.BOOL_LITERAL, "true"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val charTokenTypes = listOf(
            Token(TokenType.CHAR, "char"),
            Token(TokenType.IDENTIFIER, "c"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.CHAR_LITERAL, "a"),
            Token(TokenType.SEMICOLON, ";"),
        )

        @JvmStatic
        fun getIntAssignments() = listOf(
            Arguments.of("int a = 1;", tokenTypesOfA),
            Arguments.of("int a= 1;", tokenTypesOfA),
            Arguments.of("int a =1;", tokenTypesOfA),
            Arguments.of("int a=1;", tokenTypesOfA),
            Arguments.of("int a = 1;\nint b = 3;", tokenTypesOfA + tokenTypesOfB),
            Arguments.of("bool b = true;", booleanTokenTypes),
            Arguments.of("char c = 'a';", charTokenTypes),
        )
    }
}