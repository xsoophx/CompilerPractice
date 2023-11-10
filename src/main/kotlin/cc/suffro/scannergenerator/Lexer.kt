package cc.suffro.scannergenerator

import cc.suffro.scannergenerator.data.Token
import cc.suffro.scannergenerator.data.TokenType
import java.util.*

class Lexer {
    private var currentState = State.START
    private var currentToken = StringBuilder()

    enum class State {
        B,
        BO,
        BOO,
        BOOL,
        BR,
        BRE,
        BREA,
        BREAK,
        C,
        CH,
        CHA,
        CHAR,
        CO,
        CON,
        CONT,
        CONTI,
        CONTIN,
        CONTINU,
        CONTINUE,
        D,
        DO,
        DOU,
        DOUB,
        DOUBL,
        DOUBLE,
        E,
        EL,
        ELS,
        ELSE,
        F,
        FA,
        FAL,
        FALS,
        FL,
        FLO,
        FLOA,
        FLOAT,
        FO,
        FOR,
        I,
        IF,
        IN,
        INT,
        L,
        LO,
        LON,
        LONG,
        R,
        RE,
        RET,
        RETU,
        RETUR,
        RETURN,
        S,
        SH,
        SHO,
        SHOR,
        SHORT,
        T,
        TR,
        TRU,
        V,
        VO,
        VOI,
        VOID,
        W,
        WH,
        WHI,
        WHIL,
        WHILE,
        APOSTROPHE,
        ASSIGN,
        CLOSING_BRACKET,
        CLOSING_CURLY_BRACKET,
        DECREMENT,
        IDENTIFIER,
        INT_LITERAL,
        BOOL_LITERAL,
        CHAR_LITERAL,
        OPENING_BRACKET,
        OPENING_CURLY_BRACKET,
        PLUS,
        SEMICOLON,
        START,
        INCREMENT,
        LESS_THAN,
        MORE_THAN,
        MINUS
    }

    private fun readStates(char: Char, tokens: MutableList<Token>) {
        when (currentState) {
            State.START -> {
                when (char) {
                    'b' -> {
                        currentState = State.B
                        currentToken.append(char)
                    }

                    'c' -> {
                        currentState = State.C
                        currentToken.append(char)
                    }

                    'd' -> {
                        currentState = State.D
                        currentToken.append(char)
                    }

                    'e' -> {
                        currentState = State.E
                        currentToken.append(char)
                    }

                    'f' -> {
                        currentState = State.F
                        currentToken.append(char)
                    }

                    'i' -> {
                        currentState = State.I
                        currentToken.append(char)
                    }

                    'l' -> {
                        currentState = State.L
                        currentToken.append(char)
                    }

                    'r' -> {
                        currentState = State.R
                        currentToken.append(char)
                    }

                    's' -> {
                        currentState = State.S
                        currentToken.append(char)
                    }

                    't' -> {
                        currentState = State.T
                        currentToken.append(char)
                    }

                    'v' -> {
                        currentState = State.V
                        currentToken.append(char)
                    }

                    'w' -> {
                        currentState = State.W
                        currentToken.append(char)
                    }

                    '\'' -> {
                        currentState = State.APOSTROPHE
                    }

                    '=' -> {
                        currentState = State.ASSIGN
                        currentToken.append(char)
                    }

                    ')' -> {
                        currentState = State.CLOSING_BRACKET
                        currentToken.append(char)
                    }

                    '(' -> {
                        currentState = State.OPENING_BRACKET
                        currentToken.append(char)
                    }

                    '{' -> {
                        currentState = State.OPENING_CURLY_BRACKET
                        currentToken.append(char)
                    }

                    '}' -> {
                        currentState = State.CLOSING_CURLY_BRACKET
                        currentToken.append(char)
                    }

                    ';' -> {
                        currentState = State.SEMICOLON
                        currentToken.append(char)
                    }

                    '<' -> {
                        currentState = State.LESS_THAN
                        currentToken.append(char)
                    }

                    '>' -> {
                        currentState = State.MORE_THAN
                        currentToken.append(char)
                    }

                    '+' -> {
                        currentState = State.PLUS
                        currentToken.append(char)
                    }

                    '-' -> {
                        currentState = State.MINUS
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

            State.B -> {
                checkAndChangeState(char, mapOf('o' to State.BO, 'r' to State.BR), tokens)
            }

            State.BO -> {
                checkAndChangeState(char, mapOf('o' to State.BOO), tokens)
            }

            State.BOO -> {
                checkAndChangeState(char, mapOf('l' to State.BOOL), tokens)
            }

            State.BOOL -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.BOOL, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.BR -> {
                checkAndChangeState(char, mapOf('e' to State.BRE), tokens)
            }

            State.BRE -> {
                checkAndChangeState(char, mapOf('a' to State.BREA), tokens)
            }

            State.BREA -> {
                checkAndChangeState(char, mapOf('k' to State.BREAK), tokens)
            }

            State.BREAK -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.BREAK, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.C -> {
                checkAndChangeState(char, mapOf('h' to State.CH, 'o' to State.CO), tokens)
            }

            State.CH -> {
                checkAndChangeState(char, mapOf('a' to State.CHA), tokens)
            }

            State.CHA -> {
                checkAndChangeState(char, mapOf('r' to State.CHAR), tokens)
            }

            State.CHAR -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.CHAR, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.CO -> {
                checkAndChangeState(char, mapOf('n' to State.CON), tokens)
            }

            State.CON -> {
                checkAndChangeState(char, mapOf('t' to State.CONT), tokens)
            }

            State.CONT -> {
                checkAndChangeState(char, mapOf('i' to State.CONTI), tokens)
            }

            State.CONTI -> {
                checkAndChangeState(char, mapOf('n' to State.CONTIN), tokens)
            }

            State.CONTIN -> {
                checkAndChangeState(char, mapOf('u' to State.CONTINU), tokens)
            }

            State.CONTINU -> {
                checkAndChangeState(char, mapOf('e' to State.CONTINUE), tokens)
            }

            State.CONTINUE -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.CONTINUE, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.D -> {
                checkAndChangeState(char, mapOf('o' to State.DO), tokens)
            }

            State.DO -> {
                if (char.isWhitespace() || char == '{') {
                    tokens.add(Token(TokenType.DO, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                } else
                    checkAndChangeState(char, mapOf('u' to State.DOU), tokens)
            }

            State.DOU -> {
                checkAndChangeState(char, mapOf('b' to State.DOUB), tokens)
            }

            State.DOUB -> {
                checkAndChangeState(char, mapOf('l' to State.DOUBL), tokens)
            }

            State.DOUBL -> {
                checkAndChangeState(char, mapOf('e' to State.DOUBLE), tokens)
            }

            State.DOUBLE -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.DOUBLE, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.E -> {
                checkAndChangeState(char, mapOf('l' to State.EL), tokens)
            }

            State.EL -> {
                checkAndChangeState(char, mapOf('s' to State.ELS), tokens)
            }

            State.ELS -> {
                checkAndChangeState(char, mapOf('e' to State.ELSE), tokens)
            }

            State.ELSE -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.ELSE, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.F -> {
                checkAndChangeState(char, mapOf('a' to State.FA, 'l' to State.FL, 'o' to State.FO), tokens)
            }

            State.FA -> {
                checkAndChangeState(char, mapOf('l' to State.FAL), tokens)
            }

            State.FAL -> {
                checkAndChangeState(char, mapOf('s' to State.FALS), tokens)
            }

            State.FALS -> {
                checkAndChangeState(char, mapOf('e' to State.BOOL_LITERAL), tokens)
            }

            State.FL -> {
                checkAndChangeState(char, mapOf('o' to State.FLO), tokens)
            }

            State.FLO -> {
                checkAndChangeState(char, mapOf('a' to State.FLOA), tokens)
            }

            State.FLOA -> {
                checkAndChangeState(char, mapOf('t' to State.FLOAT), tokens)
            }

            State.FLOAT -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.FLOAT, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.FO -> {
                checkAndChangeState(char, mapOf('r' to State.FOR), tokens)
            }

            State.FOR -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.FOR, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.I -> {
                checkAndChangeState(char, mapOf('f' to State.IF, 'n' to State.IN), tokens)
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

            State.IN -> {
                checkAndChangeState(char, mapOf('t' to State.INT), tokens)
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

            State.L -> {
                checkAndChangeState(char, mapOf('o' to State.LO), tokens)
            }

            State.LO -> {
                checkAndChangeState(char, mapOf('n' to State.LON), tokens)
            }

            State.LON -> {
                checkAndChangeState(char, mapOf('g' to State.LONG), tokens)
            }

            State.LONG -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.LONG, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.R -> {
                checkAndChangeState(char, mapOf('e' to State.RE), tokens)
            }

            State.RE -> {
                checkAndChangeState(char, mapOf('t' to State.RET), tokens)
            }

            State.RET -> {
                checkAndChangeState(char, mapOf('u' to State.RETU), tokens)
            }

            State.RETU -> {
                checkAndChangeState(char, mapOf('r' to State.RETUR), tokens)
            }

            State.RETUR -> {
                checkAndChangeState(char, mapOf('n' to State.RETURN), tokens)
            }

            State.RETURN -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.RETURN, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.S -> {
                checkAndChangeState(char, mapOf('h' to State.SH), tokens)
            }

            State.SH -> {
                checkAndChangeState(char, mapOf('o' to State.SHO), tokens)
            }

            State.SHO -> {
                checkAndChangeState(char, mapOf('r' to State.SHOR), tokens)
            }

            State.SHOR -> {
                checkAndChangeState(char, mapOf('t' to State.SHORT), tokens)
            }

            State.SHORT -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.SHORT, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.T -> {
                checkAndChangeState(char, mapOf('r' to State.TR), tokens)
            }

            State.TR -> {
                checkAndChangeState(char, mapOf('u' to State.TRU), tokens)
            }

            State.TRU -> {
                checkAndChangeState(char, mapOf('e' to State.BOOL_LITERAL), tokens)
            }

            State.V -> {
                checkAndChangeState(char, mapOf('o' to State.VO), tokens)
            }

            State.VO -> {
                checkAndChangeState(char, mapOf('i' to State.VOI), tokens)
            }

            State.VOI -> {
                checkAndChangeState(char, mapOf('d' to State.VOID), tokens)
            }

            State.VOID -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.VOID, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.W -> {
                checkAndChangeState(char, mapOf('h' to State.WH), tokens)
            }

            State.WH -> {
                checkAndChangeState(char, mapOf('i' to State.WHI), tokens)
            }

            State.WHI -> {
                checkAndChangeState(char, mapOf('l' to State.WHIL), tokens)
            }

            State.WHIL -> {
                checkAndChangeState(char, mapOf('e' to State.WHILE), tokens)
            }

            State.WHILE -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.WHILE, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.APOSTROPHE -> {
                currentToken.append(char)
                currentState = State.CHAR_LITERAL
            }

            State.ASSIGN -> {
                tokens.add(Token(TokenType.ASSIGN, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.CLOSING_BRACKET -> {
                tokens.add(Token(TokenType.CLOSING_BRACKET, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.OPENING_BRACKET -> {
                tokens.add(Token(TokenType.OPENING_BRACKET, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.OPENING_CURLY_BRACKET -> {
                tokens.add(Token(TokenType.OPENING_CURLY_BRACKET, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.CLOSING_CURLY_BRACKET -> {
                tokens.add(Token(TokenType.CLOSING_CURLY_BRACKET, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.INCREMENT -> {
                tokens.add(Token(TokenType.INCREMENT, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.DECREMENT -> {
                tokens.add(Token(TokenType.DECREMENT, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.PLUS -> {
                checkAndChangeState(char, mapOf('+' to State.INCREMENT), tokens)
            }

            State.MINUS -> {
                checkAndChangeState(char, mapOf('-' to State.DECREMENT), tokens)
            }

            State.LESS_THAN -> {
                tokens.add(Token(TokenType.LESS_THAN, currentToken.toString()))
                setStartState()
                readStates(char, tokens)

            }

            State.MORE_THAN -> {
                tokens.add(Token(TokenType.MORE_THAN, currentToken.toString()))
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

            State.BOOL_LITERAL -> {
                if (char.isLetterOrDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
                } else {
                    tokens.add(Token(TokenType.BOOL_LITERAL, currentToken.toString()))
                    setStartState()
                    readStates(char, tokens)
                }
            }

            State.CHAR_LITERAL -> {
                if (char == '\'') {
                    tokens.add(Token(TokenType.CHAR_LITERAL, currentToken.toString()))
                    setStartState()
                } else {
                    throw IllegalArgumentException("Char literal must contain exactly one character!")
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

        }
    }

    private fun checkAndChangeState(char: Char, states: Map<Char, State>, tokens: MutableList<Token>) {
        currentState = states[char] ?: State.IDENTIFIER
        if (currentState != State.IDENTIFIER || char in 'a'..'z' || char in 'A'..'Z') {
            currentToken.append(char)
        } else {
            val tokenType = tokenTypeByState(currentState)
            tokens.add(Token(tokenType, currentToken.toString()))
            setStartState()
            readStates(char, tokens)
        }
    }

    private fun tokenTypeByState(state: State): TokenType {
        return TokenType.values()
            .find { it.name.uppercase(Locale.getDefault()) == state.name.uppercase(Locale.getDefault()) }
            ?: throw IllegalArgumentException("Unknown state: $state")
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
