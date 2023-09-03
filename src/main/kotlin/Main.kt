import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader

fun main() {
    generateCode()
    if (createAndExecuteJar("generatedOutput", "GeneratedCode.kt"))
        println(">>> Jar successfully executed!")
    else {
        println(">>> Jar execution failed!")
    }
}

fun generateCode() {
    val code = """
        fun main() {
            println("Hello, World!")
        }
    """.trimIndent()

    val outputFile = File("generatedOutput/GeneratedCode.kt")
    FileWriter(outputFile).use { it.write(code) }

    println("Code successfully generated!")
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
        println("Kotlin Compilation Failed")
        return false
    }
    return true
}
