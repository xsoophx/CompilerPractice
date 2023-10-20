package cc.suffro.scannergenerator.data
/*
    This class describes states, which are not splittable.
    This means, that the state is not split into multiple states (e.g. INT -> I, IN, INT), but is a single state.

    If @startStateCondition is null, the state is not a start state. It describes the condition, which needs to be fulfilled to enter the start state.
    @readStateCondition describes the condition, which need to be fulfilled for the read states (states that are not the start state).
    @hasToBeLast describes, if the state needs to be shown at the end of the state machine.
    @appendChar describes, if the char in the current state should be appended to the current token.
 */
enum class NonSplittableState(
    val startStateCondition: StateIfCondition?,
    val readStateCondition: StateIfCondition? = null,
    val hasToBeLast: Boolean = false,
    val appendChar: Boolean = true
) {
    APOSTROPHE(StringIfCondition("'\\''"), appendChar = false),
    ASSIGN(CharIfCondition('=')),
    BOOL_LITERAL(
        startStateCondition = null,
        readStateCondition = StringIfCondition("char.isLetterOrDigit()"),
        hasToBeLast = true
    ),
    CHAR_LITERAL(
        startStateCondition = null,
        readStateCondition = StringIfCondition("'\\''"),
        hasToBeLast = true,
        appendChar = false
    ),
    CLOSING_BRACKET(CharIfCondition(')')),
    IDENTIFIER(StringIfCondition("in 'a'..'z', in 'A'..'Z'"), hasToBeLast = true),
    INT_LITERAL(StringIfCondition("in '0'..'9'"), StringIfCondition("char.isDigit()"), true),
    OPENING_BRACKET(CharIfCondition('(')),
    SEMICOLON(CharIfCondition(';')),
    START(null);

    companion object {
        fun byName(name: String): NonSplittableState? {
            return values().find { it.name == name }
        }
    }
}
