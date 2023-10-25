package cc.suffro.scannergenerator.data

import cc.suffro.scannergenerator.determineAndCreateClosingBracketsExtension
import cc.suffro.scannergenerator.generators.NonSplittableStateGenerator
import java.util.*

/*
    This class describes states, which are not splittable.
    This means, that the state is not split into multiple states (e.g. INT -> I, IN, INT), but is a single state.

    If @startStateCondition is null, the state is not a start state. It describes the condition, which needs to be fulfilled to enter the start state.
    @readStateCondition describes the condition, which need to be fulfilled for the read states (states that are not the start state).
    @hasToBeLast describes, if the state needs to be shown at the end of the state machine.
    @appendChar describes, if the char in the current state should be appended to the current token.
 */
data class StartStateProperties(
    val condition: StateIfCondition,
    val appendChar: Boolean = true,
)

enum class NonSplittableState(
    val startStateProperties: StartStateProperties?,
    val readStateCondition: StateIfCondition? = null,
    val hasToBeLast: Boolean = false,
    val hasCustomState: Boolean = false
) {
    APOSTROPHE(StartStateProperties(StringIfCondition("'\\''"), appendChar = false)),
    ASSIGN(StartStateProperties(CharIfCondition('='))),
    BOOL_LITERAL(
        startStateProperties = null,
        readStateCondition = StringIfCondition("char.isLetterOrDigit()"),
        hasToBeLast = true,
    ),

    CHAR_LITERAL(
        startStateProperties = null,
        readStateCondition = StringIfCondition("char == '\\''"),
        hasToBeLast = true,
        hasCustomState = true,
    ),
    CLOSING_BRACKET(StartStateProperties(CharIfCondition(')'))),
    IDENTIFIER(
        StartStateProperties(StringIfCondition("in 'a'..'z', in 'A'..'Z'")),
        hasToBeLast = true,
        hasCustomState = true,
    ),
    INT_LITERAL(StartStateProperties(StringIfCondition("in '0'..'9'")), StringIfCondition("char.isDigit()"), true),
    OPENING_BRACKET(StartStateProperties(CharIfCondition('('))),
    SEMICOLON(StartStateProperties(CharIfCondition(';'))),
    START(null);

    companion object {
        fun byName(name: String): NonSplittableState? {
            return values().find { it.name == name }
        }
    }
}
