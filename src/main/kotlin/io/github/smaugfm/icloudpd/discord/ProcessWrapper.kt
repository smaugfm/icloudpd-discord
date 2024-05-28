package io.github.smaugfm.icloudpd.discord

import java.io.OutputStream

open class ProcessWrapper(private val cmd: String) {

  protected open fun onProcessOutput(buffer: ByteArray, len: Int) {
    println(String(buffer, 0, len));
  }

  protected open fun hookToProcessInput(input: OutputStream): OutputStream {
    return input
  }

  protected open fun beforeStart(args: Array<String>) {
    //do nothing
  }

  fun execAndBlock(args: Array<String>) {
    beforeStart(args)

    val builder = ProcessBuilder(cmd, *args)

    builder.redirectErrorStream(true)
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
//      int ni = System.in.available();
//      if (ni > 0) {
//        int n = System.in.read(buffer, 0, Math.min(ni, buffer.length));
//        in.write(buffer, 0, n);
//        in.flush();
//      }

      Thread.sleep(10)
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
