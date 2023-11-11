package cc.suffro.scannergenerator

import cc.suffro.scannergenerator.data.Token
import cc.suffro.scannergenerator.data.TokenType
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

    @ParameterizedTest
    @MethodSource("getBooleanAssignments")
    fun `should detect correct tokens for boolean assign`(assignment: String, tokenTypes: List<Token>) {
        val tokens = Lexer().tokenize(assignment)
        assertEquals(expected = tokenTypes, actual = tokens)
    }

    @ParameterizedTest
    @MethodSource("getCharAssignments")
    fun `should detect correct tokens for char assign`(assignment: String, tokenTypes: List<Token>) {
        val tokens = Lexer().tokenize(assignment)
        assertEquals(expected = tokenTypes, actual = tokens)
    }

    @ParameterizedTest
    @MethodSource("getLoopAssignments")
    fun `should detect correct loop assignments`(assignment: String, tokenTypes: List<Token>) {
        val tokens = Lexer().tokenize(assignment)
        assertEquals(expected = tokenTypes, actual = tokens)
    }

    @ParameterizedTest
    @MethodSource("chainedAssignments")
    fun `should detect correct chained assignments`(assignment: String, tokenTypes: List<Token>) {
        val tokens = TokenList(Lexer().tokenize(assignment))
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

        private val trueBooleanTokenTypes = listOf(
            Token(TokenType.BOOL, "bool"),
            Token(TokenType.IDENTIFIER, "b"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.BOOL_LITERAL, "true"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val falseBooleanTokenTypes = listOf(
            Token(TokenType.BOOL, "bool"),
            Token(TokenType.IDENTIFIER, "f"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.BOOL_LITERAL, "false"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val charTokenTypesOne = listOf(
            Token(TokenType.CHAR, "char"),
            Token(TokenType.IDENTIFIER, "c"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.CHAR_LITERAL, "a"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val charTokenTypesTwo = listOf(
            Token(TokenType.CHAR, "char"),
            Token(TokenType.IDENTIFIER, "x"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.CHAR_LITERAL, "!"),
            Token(TokenType.SEMICOLON, ";"),
        )

        private val tokenTypesXisLiteral = listOf(
            Token(TokenType.INT, "int"),
            Token(TokenType.IDENTIFIER, "x"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.IDENTIFIER, "i"),
            Token(TokenType.SEMICOLON, ";"),
        )

        @JvmStatic
        fun getIntAssignments() = listOf(
            Arguments.of("int a = 1;", tokenTypesOfA),
            Arguments.of("int a= 1;", tokenTypesOfA),
            Arguments.of("int a =1;", tokenTypesOfA),
            Arguments.of("int a=1;", tokenTypesOfA),
            Arguments.of("int x = i;", tokenTypesXisLiteral),
            Arguments.of("int a = 1;\nint b = 3;", tokenTypesOfA + tokenTypesOfB),
        )

        @JvmStatic
        fun getBooleanAssignments() = listOf(
            Arguments.of("bool b = true;", trueBooleanTokenTypes),
            Arguments.of("bool f = false;", falseBooleanTokenTypes),
        )

        @JvmStatic
        fun getCharAssignments() = listOf(
            Arguments.of("char c = 'a';", charTokenTypesOne),
            Arguments.of("char x = '!';", charTokenTypesTwo),

            )

        private val preIncrement = listOf(
            Token(TokenType.INCREMENT, "++"),
            Token(TokenType.IDENTIFIER, "i"),
        )

        private val postIncrement = listOf(
            Token(TokenType.IDENTIFIER, "i"),
            Token(TokenType.INCREMENT, "++"),
        )

        private val forLoopStart = listOf(
            Token(TokenType.FOR, "for"),
            Token(TokenType.OPENING_BRACKET, "("),
            Token(TokenType.INT, "int"),
            Token(TokenType.IDENTIFIER, "i"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.INT_LITERAL, "0"),
            Token(TokenType.SEMICOLON, ";"),
            Token(TokenType.IDENTIFIER, "i"),
            Token(TokenType.LESS_THAN, "<"),
            Token(TokenType.INT_LITERAL, "10"),
            Token(TokenType.SEMICOLON, ";")
        )

        private val forLoopClosing = listOf(
            Token(TokenType.CLOSING_BRACKET, ")"),
            Token(TokenType.OPENING_CURLY_BRACKET, "{"),
            Token(TokenType.CLOSING_CURLY_BRACKET, "}")
        )

        private val forTokenTypesPostIncrement = forLoopStart + postIncrement + forLoopClosing
        private val forTokenPreIncrement = forLoopStart + preIncrement + forLoopClosing

        private val whileTokenTypes = listOf(
            Token(TokenType.WHILE, "while"),
            Token(TokenType.OPENING_BRACKET, "("),
            Token(TokenType.IDENTIFIER, "a"),
            Token(TokenType.MORE_THAN, ">"),
            Token(TokenType.IDENTIFIER, "b"),
            Token(TokenType.CLOSING_BRACKET, ")"),
            Token(TokenType.OPENING_CURLY_BRACKET, "{"),
            Token(TokenType.CLOSING_CURLY_BRACKET, "}"),
        )

        private val doWhileTokenTypes = listOf(
            Token(TokenType.DO, "do"),
            Token(TokenType.OPENING_CURLY_BRACKET, "{"),
            Token(TokenType.CLOSING_CURLY_BRACKET, "}"),
            Token(TokenType.WHILE, "while"),
            Token(TokenType.OPENING_BRACKET, "("),
            Token(TokenType.IDENTIFIER, "a"),
            Token(TokenType.MORE_THAN, ">"),
            Token(TokenType.IDENTIFIER, "b"),
            Token(TokenType.CLOSING_BRACKET, ")"),
            Token(TokenType.SEMICOLON, ";"),
        )

        @JvmStatic
        fun getLoopAssignments() = listOf(
            Arguments.of("for (int i = 0; i < 10; ++i) { }", forTokenPreIncrement),
            Arguments.of("for (int i = 0; i < 10; i++) { }", forTokenTypesPostIncrement),
            Arguments.of("while (a > b) { }", whileTokenTypes),
            Arguments.of("do { } while (a > b);", doWhileTokenTypes),
        )

        private val chainedFunctions = TokenList(
            listOf(
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "add"),
                Token(TokenType.OPENING_BRACKET, "("),
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "a"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "b"),
                Token(TokenType.CLOSING_BRACKET, ")"),
                Token(TokenType.OPENING_CURLY_BRACKET, "{"),
                Token(TokenType.RETURN, "return"),
                Token(TokenType.IDENTIFIER, "a"),
                Token(TokenType.PLUS, "+"),
                Token(TokenType.IDENTIFIER, "b"),
                Token(TokenType.SEMICOLON, ";"),
                Token(TokenType.CLOSING_CURLY_BRACKET, "}"),
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "main"),
                Token(TokenType.OPENING_BRACKET, "("),
                Token(TokenType.CLOSING_BRACKET, ")"),
                Token(TokenType.OPENING_CURLY_BRACKET, "{"),
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "result"),
                Token(TokenType.ASSIGN, "="),
                Token(TokenType.IDENTIFIER, "add"),
                Token(TokenType.OPENING_BRACKET, "("),
                Token(TokenType.INT_LITERAL, "3"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.INT_LITERAL, "4"),
                Token(TokenType.CLOSING_BRACKET, ")"),
                Token(TokenType.SEMICOLON, ";"),
                Token(TokenType.RETURN, "return"),
                Token(TokenType.INT_LITERAL, "0"),
                Token(TokenType.SEMICOLON, ";"),
                Token(TokenType.CLOSING_CURLY_BRACKET, "}"),
            )
        )

        private val arrayAssignments = TokenList(
            listOf(
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "numbers"),
                Token(TokenType.OPENING_SQUARE_BRACKET, "["),
                Token(TokenType.CLOSING_SQUARE_BRACKET, "]"),
                Token(TokenType.ASSIGN, "="),
                Token(TokenType.OPENING_CURLY_BRACKET, "{"),
                Token(TokenType.INT_LITERAL, "1"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.INT_LITERAL, "2"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.INT_LITERAL, "3"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.INT_LITERAL, "4"),
                Token(TokenType.COMMA, ","),
                Token(TokenType.INT_LITERAL, "5"),
                Token(TokenType.CLOSING_CURLY_BRACKET, "}"),
                Token(TokenType.SEMICOLON, ";"),
                Token(TokenType.INT, "int"),
                Token(TokenType.IDENTIFIER, "thirdNumber"),
                Token(TokenType.ASSIGN, "="),
                Token(TokenType.IDENTIFIER, "numbers"),
                Token(TokenType.OPENING_SQUARE_BRACKET, "["),
                Token(TokenType.INT_LITERAL, "2"),
                Token(TokenType.CLOSING_SQUARE_BRACKET, "]"),
                Token(TokenType.SEMICOLON, ";"),
            )
        )

        @JvmStatic
        fun chainedAssignments() = listOf(
            Arguments.of(
                "int add(int a, int b) {\nreturn a + b;\n}\nint main() {\nint result = add(3, 4);\nreturn 0;\n}",
                chainedFunctions
            ),
            Arguments.of(
                "int numbers[] = {1, 2, 3, 4, 5};\nint thirdNumber = numbers[2];",
                arrayAssignments
            )
        )
    }
}

data class TokenList(val tokens: List<Token>) : List<Token> by tokens {
    override fun toString(): String {
        return tokens.joinToString("\n")
    }
}