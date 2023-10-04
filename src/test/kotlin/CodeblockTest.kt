import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CodeblockTest {

    companion object {
        private const val codeClass = "class Test"
        private const val function = "fun test()"
        private val codeBlock = Codeblock(preCode = codeClass, codeLines = function)
        private val expectedTestClass = """
            class Test {
                fun test()
            }
        """.trimIndent()

        private const val lineOfCode = "val a = 1"
        private val codeBlockWithLineOfCode = Codeblock(
            preCode = codeClass,
            codeLines = Codeblock(preCode = function, codeLines = lineOfCode)
        )
        private val expectedTestClassWithLineOfCode = """
            class Test {
                fun test() {
                    val a = 1
                }
            }
        """.trimIndent()

        @JvmStatic
        fun getCodeFragments(): Stream<Arguments> = Stream.of(
            Arguments.of(codeBlock, expectedTestClass),
            Arguments.of(codeBlockWithLineOfCode, expectedTestClassWithLineOfCode),
        )
    }
}