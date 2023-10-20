package cc.suffro.scannergenerator

import cc.suffro.scannergenerator.data.Token
import cc.suffro.scannergenerator.data.TokenType

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
        IDENTIFIER,
        INT_LITERAL,
        BOOL_LITERAL,
        CHAR_LITERAL,
        OPENING_BRACKET,
        SEMICOLON,
        START,
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

            State.B -> {
                checkAndChangeState(char, mapOf('o' to State.BO, 'r' to State.BR))
            }

            State.BO -> {
                checkAndChangeState(char, mapOf('o' to State.BOO))
            }

            State.BOO -> {
                checkAndChangeState(char, mapOf('l' to State.BOOL))
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
                checkAndChangeState(char, mapOf('e' to State.BRE))
            }

            State.BRE -> {
                checkAndChangeState(char, mapOf('a' to State.BREA))
            }

            State.BREA -> {
                checkAndChangeState(char, mapOf('k' to State.BREAK))
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
                checkAndChangeState(char, mapOf('h' to State.CH, 'o' to State.CO))
            }

            State.CH -> {
                checkAndChangeState(char, mapOf('a' to State.CHA))
            }

            State.CHA -> {
                checkAndChangeState(char, mapOf('r' to State.CHAR))
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
                checkAndChangeState(char, mapOf('n' to State.CON))
            }

            State.CON -> {
                checkAndChangeState(char, mapOf('t' to State.CONT))
            }

            State.CONT -> {
                checkAndChangeState(char, mapOf('i' to State.CONTI))
            }

            State.CONTI -> {
                checkAndChangeState(char, mapOf('n' to State.CONTIN))
            }

            State.CONTIN -> {
                checkAndChangeState(char, mapOf('u' to State.CONTINU))
            }

            State.CONTINU -> {
                checkAndChangeState(char, mapOf('e' to State.CONTINUE))
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
                checkAndChangeState(char, mapOf('o' to State.DO))
            }

            State.DO -> {
                checkAndChangeState(char, mapOf('u' to State.DOU))
            }

            State.DOU -> {
                checkAndChangeState(char, mapOf('b' to State.DOUB))
            }

            State.DOUB -> {
                checkAndChangeState(char, mapOf('l' to State.DOUBL))
            }

            State.DOUBL -> {
                checkAndChangeState(char, mapOf('e' to State.DOUBLE))
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
                checkAndChangeState(char, mapOf('l' to State.EL))
            }

            State.EL -> {
                checkAndChangeState(char, mapOf('s' to State.ELS))
            }

            State.ELS -> {
                checkAndChangeState(char, mapOf('e' to State.ELSE))
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
                checkAndChangeState(char, mapOf('a' to State.FA, 'l' to State.FL, 'o' to State.FO))
            }

            State.FA -> {
                checkAndChangeState(char, mapOf('l' to State.FAL))
            }

            State.FAL -> {
                checkAndChangeState(char, mapOf('s' to State.FALS))
            }

            State.FALS -> {
                checkAndChangeState(char, mapOf('e' to State.BOOL_LITERAL))
            }

            State.FL -> {
                checkAndChangeState(char, mapOf('o' to State.FLO))
            }

            State.FLO -> {
                checkAndChangeState(char, mapOf('a' to State.FLOA))
            }

            State.FLOA -> {
                checkAndChangeState(char, mapOf('t' to State.FLOAT))
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
                checkAndChangeState(char, mapOf('r' to State.FOR))
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
                checkAndChangeState(char, mapOf('f' to State.IF, 'n' to State.IN))
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

            State.L -> {
                checkAndChangeState(char, mapOf('o' to State.LO))
            }

            State.LO -> {
                checkAndChangeState(char, mapOf('n' to State.LON))
            }

            State.LON -> {
                checkAndChangeState(char, mapOf('g' to State.LONG))
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
                checkAndChangeState(char, mapOf('e' to State.RE))
            }

            State.RE -> {
                checkAndChangeState(char, mapOf('t' to State.RET))
            }

            State.RET -> {
                checkAndChangeState(char, mapOf('u' to State.RETU))
            }

            State.RETU -> {
                checkAndChangeState(char, mapOf('r' to State.RETUR))
            }

            State.RETUR -> {
                checkAndChangeState(char, mapOf('n' to State.RETURN))
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
                checkAndChangeState(char, mapOf('h' to State.SH))
            }

            State.SH -> {
                checkAndChangeState(char, mapOf('o' to State.SHO))
            }

            State.SHO -> {
                checkAndChangeState(char, mapOf('r' to State.SHOR))
            }

            State.SHOR -> {
                checkAndChangeState(char, mapOf('t' to State.SHORT))
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
                checkAndChangeState(char, mapOf('r' to State.TR))
            }

            State.TR -> {
                checkAndChangeState(char, mapOf('u' to State.TRU))
            }

            State.TRU -> {
                checkAndChangeState(char, mapOf('e' to State.BOOL_LITERAL))
            }

            State.V -> {
                checkAndChangeState(char, mapOf('o' to State.VO))
            }

            State.VO -> {
                checkAndChangeState(char, mapOf('i' to State.VOI))
            }

            State.VOI -> {
                checkAndChangeState(char, mapOf('d' to State.VOID))
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
                checkAndChangeState(char, mapOf('h' to State.WH))
            }

            State.WH -> {
                checkAndChangeState(char, mapOf('i' to State.WHI))
            }

            State.WHI -> {
                checkAndChangeState(char, mapOf('l' to State.WHIL))
            }

            State.WHIL -> {
                checkAndChangeState(char, mapOf('e' to State.WHILE))
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

            State.SEMICOLON -> {
                tokens.add(Token(TokenType.SEMICOLON, currentToken.toString()))
                setStartState()
                readStates(char, tokens)
            }

            State.INT_LITERAL -> {
                if (char.isDigit()) {
                    currentToken.append(char)
                    currentState = State.IDENTIFIER
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

    private fun checkAndChangeState(char: Char, states: Map<Char, State>) {
        currentState = states[char] ?: State.IDENTIFIER
        if (currentState != State.IDENTIFIER || char in 'a'..'z' || char in 'A'..'Z') {
            currentToken.append(char)
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
        println(token)
    }
}
