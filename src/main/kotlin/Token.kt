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
    // keywords
    BOOL,
    BREAK,
    CONTINUE,

    // add '' to char
    CHAR,
    DO,
    DOUBLE,
    ELSE,
    FLOAT,
    FOR,
    IF,
    INT,
    LONG,
    RETURN,
    SHORT,
    VOID,
    WHILE,

    // symbols
    SEMICOLON(TokenClass.SYMBOL),
    OPENING_BRACKET(TokenClass.SYMBOL),
    CLOSING_BRACKET(TokenClass.SYMBOL),
    APOSTROPHE(TokenClass.SYMBOL),
    ASSIGN(TokenClass.SYMBOL),

    // literals
    INT_LITERAL(TokenClass.LITERAL),

    IDENTIFIER(TokenClass.IDENTIFIER);

    fun splitKeywordToStatesWithTokenType(keywords: Sequence<TokenType> = tokenTypeKeywords): Map<String, TokenType?> {
        return keywords.map { splitKeywordToStatesWithTokenType(it) }.reduce { acc, map -> acc + map }
            .toSortedMap()
    }

    private fun splitKeywordToStatesWithTokenType(keyword: TokenType): Map<String, TokenType?> {
        return (1..keyword.name.length)
            .asSequence()
            .map { length ->
                if (length == keyword.name.length) {
                    keyword.name.take(length) to this
                } else {
                    keyword.name.take(length) to null
                }
            }.toMap()
    }

    companion object {
        val tokenTypeKeywords = values().asSequence().filter { it.clazz == TokenClass.KEYWORD }
        fun splitKeywordToStates(keyword: TokenType): Sequence<String> {
            return (1..keyword.name.length)
                .asSequence()
                .map { keyword.name.take(it) }
        }
    }
}