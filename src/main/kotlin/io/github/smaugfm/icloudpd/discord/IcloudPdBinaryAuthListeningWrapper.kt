package io.github.smaugfm.icloudpd.discord

import java.io.OutputStream
import kotlin.system.exitProcess

class IcloudPdBinaryAuthListeningWrapper(private val handleTwoStepAuth: () -> String) :
  ProcessWrapper("icloudpd") {
  private lateinit var input: OutputStream
  private lateinit var wasFound: () -> Boolean

  private val searchingBuffer =
    StringSearchingCircularBuffer("Please enter two-factor authentication code:")

  override fun onProcessOutput(buffer: ByteArray, len: Int) {
    super.onProcessOutput(buffer, len)
    if (searchingBuffer.find(buffer, 0, len)) {
      val code = handleTwoStepAuth()
      input.write((code + "\n").toByteArray())
      input.flush()
      println("written $code to process stdin")
    }
  }

  override fun hookToProcessInput(input: OutputStream): OutputStream =
    input.also {
      this.input = input
    }

  override fun beforeStart(args: Array<String>) {
    if (!(args.contains("-u") || args.contains("--username")) &&
      !(args.contains("-p") || args.contains("--password"))
    ) {
      System.err.println(
        "icloudpd-discord: must contain '-u' or '--username' and '-p' or '--password' arguments. " +
            "Interactive username/password input to icloudpd is not supported"
      )
      exitProcess(1)
    }
  }
}
