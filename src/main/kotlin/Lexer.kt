class Lexer {
    private var currentState = State.START
    private var currentToken = StringBuilder()

    enum class State {
        ASSIGN,
        I,
        IDENTIFIER,
        IF,
        IN,
        INT,
        INT_LITERAL,
        SEMICOLON,
        START,
    }

    private fun readStates(char: Char, tokens: MutableList<Token>) {
        when (currentState) {
            State.START -> {
                when (char) {
                    'i' -> {
                        currentState = State.I
                        currentToken.append(char)
                    }

                    '=' -> {
                        currentState = State.ASSIGN
                        currentToken.append(char)
                    }

                    ';' -> {
                        currentState = State.SEMICOLON
                        currentToken.append(char)
                    }

                    in 'a'..'z', in 'A'..'Z' -> {
                        currentState = State.IDENTIFIER
                        currentToken.append(char)
                    }

                    in '0'..'9' -> {
                        currentState = State.INT_LITERAL
                        currentToken.append(char)
                    }
                }
            }

            State.I -> {
                checkAndChangeState(char, mapOf('n' to State.IN, 'f' to State.IF))
            }

            State.IN -> {
                checkAndChangeState(char, mapOf('t' to State.INT))
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

            State.IF -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.IF, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }
        }
    }

    private fun checkAndChangeState(char: Char, states: Map<Char, State>) {
        currentState = states[char] ?: State.IDENTIFIER
        currentToken.append(char)
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
        println(token)
    }
}
