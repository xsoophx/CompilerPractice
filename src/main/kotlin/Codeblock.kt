data class Codeblock(
    val preCode: Sequence<String> = emptySequence(),
    val codeLines: Sequence<Codeblock> = emptySequence(),
    val postCode: Sequence<String> = emptySequence(),
    val baseIndentation: Int = 0
) {
    constructor(
        preCode: String,
        codeLines: String,
        postCode: String
    ) : this(
        preCode = sequenceOf(preCode),
        codeLines = sequenceOf(Codeblock(preCode = sequenceOf(codeLines))),
        postCode = sequenceOf(postCode)
    )

    constructor(
        preCode: String,
        codeLines: String,
        postCode: Sequence<String> = emptySequence()
    ) : this(
        preCode = sequenceOf(preCode),
        codeLines = sequenceOf(Codeblock(preCode = sequenceOf(codeLines))),
        postCode = postCode
    )

    constructor(
        preCode: Sequence<String> = emptySequence(),
        codeLines: Sequence<Codeblock> = emptySequence(),
        postCode: String
    ) : this(
        preCode = preCode,
        codeLines = codeLines,
        postCode = sequenceOf(postCode)
    )

    constructor(
        preCode: String,
        codeLines: Codeblock,
        postCode: Sequence<String> = emptySequence()
    ) : this(
        preCode = sequenceOf(preCode),
        codeLines = sequenceOf(codeLines),
        postCode = postCode
    )

    constructor(
        preCode: String,
        codeLines: Codeblock,
        postCode: String
    ) : this(
        preCode = sequenceOf(preCode),
        codeLines = sequenceOf(codeLines),
        postCode = postCode
    )

    private fun addBrackets(): Pair<List<String>, List<String>> {
        val preCodeAsList = preCode.toMutableList().let {
            if (it.isNotEmpty()) {
                it[it.size - 1] += " {"
            }
            it
        }

        val postCodeAsList = postCode.toMutableList().let {
            it.add(0, "}")
            it
        }

        return preCodeAsList.toList() to postCodeAsList.toList()
    }
}
