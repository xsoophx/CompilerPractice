fun Pair<Int, String>.addIndentation(): String {
    return " ".repeat(first) + second
}

fun String.addIndentation(indentation: Int): String {
    return this.let { " ".repeat(indentation) + it }

}