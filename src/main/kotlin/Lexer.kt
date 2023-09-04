class Lexer {
    private var currentState = State.START
    private var currentToken = StringBuilder()

    enum class State {
        START,
        I,
        IN,
        INT,
        INT_LITERAL,
        IDENTIFIER,
        ASSIGN,
        SEMICOLON,
    }

    private fun readStates(char: Char, tokens: MutableList<Token>) {
        when (currentState) {
            State.START -> {
                if (char == 'i') {
                    currentState = State.I
                    currentToken.append(char)
                } else if (char == '=') {
                    currentState = State.ASSIGN
                    currentToken.append(char)
                } else if (char == ';') {
                    currentState = State.SEMICOLON
                    currentToken.append(char)
                } else if (char.isLetter()) {
                    currentState = State.IDENTIFIER
                    currentToken.append(char)
                } else if (char.isDigit()) {
                    currentState = State.INT_LITERAL
                    currentToken.append(char)
                }
            }

            // keep for now to make it easier to auto-generate the code
            State.I -> {
                if (char == 'n') {
                    currentState = State.IN
                } else {
                    currentState = State.IDENTIFIER
                }
                currentToken.append(char)
            }

            State.IN -> {
                if (char == 't') {
                    currentState = State.INT
                } else {
                    currentState = State.IDENTIFIER
                }
                currentToken.append(char)
            }

            State.INT -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.INT, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.IDENTIFIER -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                } else {
                    tokens.add(Token(TokenType.IDENTIFIER, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.ASSIGN -> {
                tokens.add(Token(TokenType.ASSIGN, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.SEMICOLON -> {
                tokens.add(Token(TokenType.SEMICOLON, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.INT_LITERAL -> {
                if (char.isDigit()) {
                    currentToken.append(char)
                } else {
                    tokens.add(Token(TokenType.INT_LITERAL, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }
        }
    }

    private fun setStartState() {
        currentToken = StringBuilder()
        currentState = State.START
    }

    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()

        for (char in input) {
            readStates(char, tokens)
        }

        // trigger detection of last token
        readStates(' ', tokens)

        return tokens
    }
}

fun main() {
    val input = "int abc=1;"
    val lexer = Lexer()
    val tokens = lexer.tokenize(input)

    for (token in tokens) {
        println("Token Type: ${token.type}, Value: ${token.value}")
    }
}
