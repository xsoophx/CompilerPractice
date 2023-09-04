data class Token(val type: TokenType, val value: String)

enum class TokenType {
    INT,
    INT_LITERAL,
    IDENTIFIER,
    ASSIGN,
    SEMICOLON
}