enum class KeywordState(private val value: String) {
    BOOL("bool"),
    DOUBLE("double"),
    INT("int"),
    BREAK("break"),
    CONTINUE("continue"),
    ELSE("else"),
    FOR("for"),
    IF("if"),
    RETURN("return"),
    WHILE("while"),
    VOID("void"),
    DO("do"),
    FLOAT("float"),
    LONG("long"),
    SHORT("short"),
    ;

    override fun toString(): String {
        val splits = splitKeywordToStates()
        return splits.joinToString(separator = "\n")
    }

    fun splitKeywordToStates(): List<String> {
        return (1..this.value.length)
            .asSequence()
            .map { this.value.take(it) }
            .toList()
    }
}