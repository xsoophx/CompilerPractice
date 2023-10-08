const val EMPTY_LINE = ""

fun Pair<Int, String>.addIndentation(): String {
    return " ".repeat(first) + second
}

fun String.addIndentation(indentation: Int): String {
    return let { " ".repeat(indentation) + it }
}
