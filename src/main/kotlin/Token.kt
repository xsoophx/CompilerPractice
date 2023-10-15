data class Token(val type: TokenType, val value: String) {
    override fun toString(): String {
        return "Token Type: $type, Value: $value"
    }
}

enum class TokenClass {
    LITERAL,
    IDENTIFIER,
    KEYWORD,
    SYMBOL,
}

enum class TokenType(val clazz: TokenClass = TokenClass.KEYWORD) {
    ASSIGN(TokenClass.SYMBOL),
    IDENTIFIER(TokenClass.IDENTIFIER),
    IF,
    INT,
    INT_LITERAL(TokenClass.LITERAL),
    SEMICOLON(TokenClass.SYMBOL),
    OPENING_BRACKET(TokenClass.SYMBOL),
    CLOSING_BRACKET(TokenClass.SYMBOL),
    APOSTROPHE(TokenClass.SYMBOL),
}