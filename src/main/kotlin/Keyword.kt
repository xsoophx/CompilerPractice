

enum class Keyword(private val value: String) {
    BOOL("bool"),
    BREAK("break"),
    CONTINUE("continue"),
    DO("do"),
    DOUBLE("double"),
    ELSE("else"),
    FLOAT("float"),
    FOR("for"),
    IF("if"),
    INT("int"),
    LONG("long"),
    RETURN("return"),
    SHORT("short"),
    VOID("void"),
    WHILE("while"),
    ;

    override fun toString(): String {
        val splits = splitKeywordToStates()
        return splits.joinToString(separator = "\n")
    }

    fun splitKeywordToStates(): Sequence<String> {
        return (1..value.length)
            .asSequence()
            .map { value.take(it) }
    }
}