object HelperFunctions {
    fun Pair<Int, String>.addIndentation(): String {
        return " ".repeat(first) + second
    }
}