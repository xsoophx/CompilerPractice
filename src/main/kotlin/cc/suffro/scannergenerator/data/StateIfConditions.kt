package cc.suffro.scannergenerator.data

interface StateIfCondition

data class CharIfCondition(val char: Char) : StateIfCondition {
    override fun toString(): String {
        return char.toString()
    }
}

data class StringIfCondition(val string: String) : StateIfCondition {
    override fun toString(): String {
        return string
    }
}