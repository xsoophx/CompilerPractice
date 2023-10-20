package cc.suffro.scannergenerator.generators

import cc.suffro.scannergenerator.data.Generator
import cc.suffro.scannergenerator.data.NonSplittableState
import cc.suffro.scannergenerator.data.TokenType
import cc.suffro.scannergenerator.determineAndCreateClosingBrackets
import java.util.*

class StateEnumClassGenerator(private val states: List<Pair<TokenType, String>>) : Generator {
    fun generate(): Sequence<String> {
        val code = sequenceOf("enum class State {") + generateEnumEntries()
        return code + determineAndCreateClosingBrackets(code)
    }

    private fun generateEnumEntries(): List<String> {
        val states = getKeywordStatesAsString() + NonSplittableState.values()
            .map { it.name.uppercase(Locale.getDefault()) }.sorted()

        return states.map { "$it," }
    }


    private fun getKeywordStatesAsString(): List<String> {
        return states.map { it.second.uppercase(Locale.getDefault()) }
    }
}