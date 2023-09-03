import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader

fun main() {
    val generatedCode = """
        fun main() {
            println("Hello, World!")
        }
    """.trimIndent()


    if (generateCode(generatedCode)) {
        println(">>> Code successfully generated!")
    } else {
        println(">>> Code generation failed!")
    }
}

private fun generateCode(code: String): Boolean {
    val outputDir = File("generatedOutput")
    outputDir.mkdir()

    val codeFile = File(outputDir, "GeneratedCode.kt")
    FileWriter(codeFile).use { it.write(code) }

    val processBuilder = ProcessBuilder("kotlinc", "-include-runtime", "-d", "GeneratedCode.jar", codeFile.absolutePath)
    val process = processBuilder.start()
    process.waitFor()

    if (process.exitValue() == 0) {
        val runtime = Runtime.getRuntime()
        val execProcess = runtime.exec("java -jar GeneratedCode.jar")

        val inputStream = execProcess.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line: String? = reader.readLine()
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
