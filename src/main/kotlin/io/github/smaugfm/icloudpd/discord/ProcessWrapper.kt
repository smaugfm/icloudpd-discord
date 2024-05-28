package io.github.smaugfm.icloudpd.discord

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.OutputStream

private val logger = KotlinLogging.logger {}

open class ProcessWrapper(private val cmd: String) {

  protected open suspend fun onProcessOutput(buffer: ByteArray, len: Int) {
    //do nothing
  }

  protected open fun hookToProcessInput(input: OutputStream): OutputStream {
    return input
  }

  protected open fun beforeStart(args: Array<String>) {
    //do nothing
  }

  suspend fun execAndBlock(args: Array<String>): Int {
    beforeStart(args)

    val builder = ProcessBuilder(cmd, *args)

    builder.redirectErrorStream(true)
    return withContext(Dispatchers.IO) {
      val process = builder.start()
      val output = process.inputStream
      hookToProcessInput(process.outputStream)
      val buffer = ByteArray(4096)

      while (isAlive(process)) {
        val no = output.available()
        if (no > 0) {
          val size = no.coerceAtMost(buffer.size)
          val n = output.read(buffer, 0, size)

          onProcessOutput(buffer, n)
        }

        delay(10)
      }
      process.exitValue()
    }
  }

  private fun isAlive(process: Process): Boolean {
    try {
      process.exitValue()
      return false
    } catch (e: IllegalThreadStateException) {
      return true
    }
  }
}
