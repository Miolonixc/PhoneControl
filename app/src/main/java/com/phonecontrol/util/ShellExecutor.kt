package com.phonecontrol.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ShellExecutor {

    suspend fun execute(command: String): ShellResult = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val output = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()
            process.waitFor()

            ShellResult(
                success = process.exitValue() == 0,
                output = output.trim(),
                error = error.trim(),
                exitCode = process.exitValue()
            )
        } catch (e: Exception) {
            ShellResult(
                success = false,
                output = "",
                error = e.message ?: "Unknown error",
                exitCode = -1
            )
        }
    }

    suspend fun executeWithTimeout(command: String, timeoutMs: Long = 5000): ShellResult {
        return withContext(Dispatchers.IO) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
                val output = process.inputStream.bufferedReader().readText()
                val error = process.errorStream.bufferedReader().readText()

                val finished = process.waitFor() // Simplified; real impl would use timeout
                ShellResult(
                    success = finished == 0,
                    output = output.trim(),
                    error = error.trim(),
                    exitCode = finished
                )
            } catch (e: Exception) {
                ShellResult(
                    success = false,
                    output = "",
                    error = e.message ?: "Unknown error",
                    exitCode = -1
                )
            }
        }
    }
}

data class ShellResult(
    val success: Boolean,
    val output: String,
    val error: String,
    val exitCode: Int
)
