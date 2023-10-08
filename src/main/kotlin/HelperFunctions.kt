const val EMPTY_LINE = ""

fun Pair<Int, String>.addIndentation(): String {
    return " ".repeat(first) + second
}

fun String.addIndentation(indentation: Int): String {
    return let { " ".repeat(indentation) + it }
}

fun Sequence<String>.addIndentation(indentation: Int): String {
    return map { it.addIndentation(indentation) }.joinToString(separator = "\n")
}

fun List<String>.addIndentation(indentation: Int): String {
    return joinToString(separator = "\n") { it.addIndentation(indentation) }
}