package cc.suffro.scannergenerator

import cc.suffro.scannergenerator.generators.CodeGenerator
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
    val code = CodeGenerator().generate()

    val outputFile = File("generatedOutput/GeneratedCode.kt")
    FileWriter(outputFile).use { it.write(code) }

    logger.info { "Code successfully generated!" }
}

private fun createAndExecuteJar(pathName: String, child: String): Boolean {
    val outputDir = File(pathName)
    if (outputDir.mkdir()) logger.info { ">>> Directory $pathName created!" }

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
