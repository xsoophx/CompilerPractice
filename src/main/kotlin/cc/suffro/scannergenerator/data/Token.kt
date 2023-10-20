package cc.suffro.scannergenerator.data

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

data class OmitCharInformation(val omitLastCharInState: Boolean, val nextTokenType: TokenType)

enum class TokenType(val clazz: TokenClass = TokenClass.KEYWORD, val omitCharInformation: OmitCharInformation? = null) {
    // literals
    BOOL_LITERAL(TokenClass.LITERAL),
    CHAR_LITERAL(TokenClass.LITERAL),
    DOUBLE_LITERAL(TokenClass.LITERAL),
    FLOAT_LITERAL(TokenClass.LITERAL),
    INT_LITERAL(TokenClass.LITERAL),
    LONG_LITERAL(TokenClass.LITERAL),

    // keywords
    BOOL,
    BREAK,
    CONTINUE,

    // add '' to char
    CHAR,
    DO,
    DOUBLE,
    ELSE,
    FALSE(omitCharInformation = OmitCharInformation(omitLastCharInState = true, nextTokenType = BOOL_LITERAL)),
    FLOAT,
    FOR,
    IF,
    INT,
    LONG,
    RETURN,
    SHORT,
    TRUE(omitCharInformation = OmitCharInformation(omitLastCharInState = true, nextTokenType = BOOL_LITERAL)),
    VOID,
    WHILE,

    // symbols
    APOSTROPHE(TokenClass.SYMBOL),
    ASSIGN(TokenClass.SYMBOL),
    CLOSING_BRACKET(TokenClass.SYMBOL),
    OPENING_BRACKET(TokenClass.SYMBOL),
    SEMICOLON(TokenClass.SYMBOL),

    IDENTIFIER(TokenClass.IDENTIFIER);


    companion object {
        val tokenTypeKeywords = values().asSequence().filter { it.clazz == TokenClass.KEYWORD }

        fun splitKeywordToStates(keyword: TokenType): List<Pair<TokenType, String>> {
            val maximalLength = keyword.omitCharInformation?.let { keyword.name.length - 1 } ?: keyword.name.length
            return (1..maximalLength)
                .asSequence()
                .map { keyword to keyword.name.take(it) }
                .toList()
        }

        private fun splitKeywordToStatesWithTokenType(keyword: TokenType): List<SplitStateInformation> {
            val maximalLength = keyword.omitCharInformation?.let { keyword.name.length - 1 } ?: keyword.name.length
            return (1..maximalLength)
                .asSequence()
                .map { length ->
                    if (length == keyword.name.length) {
                        // TODO: write cleaner
                        SplitStateInformation(
                            splitState = keyword.name.take(length),
                            originalType = keyword,
                            isFinalState = true,
                            followUpState = keyword.omitCharInformation?.nextTokenType
                        )
                    } else {
                        SplitStateInformation(
                            splitState = keyword.name.take(length),
                            originalType = keyword,
                            isFinalState = false,
                            followUpState = keyword.omitCharInformation?.nextTokenType

                        )
                    }
                }.toList()
        }
    }
}

data class SplitStateInformation(
    val splitState: String,
    val originalType: TokenType,
    val isFinalState: Boolean,
    val followUpState: TokenType? = null,
)