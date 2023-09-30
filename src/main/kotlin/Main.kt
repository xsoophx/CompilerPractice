import HelperFunctions.addIndentation
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
    val startTime = System.currentTimeMillis()

    generateCode()
    if (createAndExecuteJar("generatedOutput", "GeneratedCode.kt"))
        logger.info { ">>> Jar successfully executed!" }
    else {
        logger.error { ">>> Jar execution failed!" }
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime

    logger.info { ">>> Ausführungszeit: $executionTime Millisekunden" }
}

fun generateCode() {
    val code = sequenceOf(
        0 to "fun main() {",
        4 to "println(\"Hello, World!\")",
        4 to "State.values().forEach { println(it) }",
        0 to "}\n",
        0 to CodeGenerator().generate()
    ).map { it.addIndentation() }.joinToString(separator = "\n")

    val outputFile = File("generatedOutput/GeneratedCode.kt")
    FileWriter(outputFile).use { it.write(code) }

    logger.info { "Code successfully generated!" }
}

private fun createAndExecuteJar(pathName: String, child: String): Boolean {
    val outputDir = File(pathName)
    outputDir.mkdir()

    val codeFile = File(outputDir, child)

    val processBuilder = ProcessBuilder("kotlinc", "-include-runtime", "-d", "GeneratedCode.jar", codeFile.absolutePath)
    val process = processBuilder.start()
    process.waitFor()

    if (process.exitValue() == 0) {
        val runtime = Runtime.getRuntime()
        val execProcess = runtime.exec("java -jar GeneratedCode.jar")

        val inputStream = execProcess.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line = reader.readLine()
        while (line != null) {
            println(line)
            line = reader.readLine()
        }

        execProcess.waitFor()
    } else {
        return false
    }

    return true
}
