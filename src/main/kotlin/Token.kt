data class Token(val type: TokenType, val value: String) {
    override fun toString(): String {
        return "Token Type: $type, Value: $value"
    }
}

enum class TokenType {
    ASSIGN,
    IDENTIFIER,
    IF,
    INT,
    INT_LITERAL,
    SEMICOLON
}