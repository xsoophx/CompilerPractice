data class Codeblock(
    val preCode: Sequence<String> = emptySequence(),
    val codeLines: Sequence<Codeblock> = emptySequence(),
    val postCode: Sequence<String> = emptySequence(),
    val baseIndentation: Int = 0
) {
    constructor(
        preCode: String,
        codeLines: Sequence<Codeblock>,
        postCode: String
    ) : this(
        preCode = sequenceOf(preCode),
        codeLines = codeLines,
        postCode = sequenceOf(postCode)
    )

    constructor(
        preCode: Sequence<String>,
        codeLines: Sequence<Codeblock>,
        postCode: String
    ) : this(
        preCode = preCode,
        codeLines = codeLines,
        postCode = sequenceOf(postCode)
    )

    constructor(
        preCode: String,
        codeLines: Codeblock,
        postCode: String
    ) : this(
        preCode = sequenceOf(preCode),
        codeLines = sequenceOf(codeLines),
        postCode = sequenceOf(postCode)
    )

    fun toString(indentation: Int = 0): String {
        return (preCode +
                "{" +
                codeLines.map { toString(indentation + STANDARD_INDENTATION) } +
                "}" +
                postCode).joinToString(
            "\n"
        )
    }
}
