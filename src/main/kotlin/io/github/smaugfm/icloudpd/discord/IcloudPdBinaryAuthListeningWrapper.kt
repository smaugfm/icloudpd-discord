package io.github.smaugfm.icloudpd.discord

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

private val logger = KotlinLogging.logger("icloudpd")

class IcloudPdBinaryAuthListeningWrapper(
  private val handleTwoStepAuth: suspend () -> String
) : ProcessWrapper("icloudpd") {
  private lateinit var input: OutputStream

  private val searchingBuffer =
    StringSearchingCircularBuffer("Please enter two-factor authentication code:")

  override suspend fun onProcessOutput(buffer: ByteArray, len: Int) {
    super.onProcessOutput(buffer, len)
    logger.info { String(buffer, 0, len) }

    if (searchingBuffer.feedAndFind(buffer, 0, len)) {
      val code = handleTwoStepAuth()
      withContext(Dispatchers.IO) {
        input.write((code + "\n").toByteArray())
        input.flush()
      }
      logger.info { "written $code to icloudpd stdin" }
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
      fail(
        "icloudpd-discord: must contain '-u' or '--username' and '-p' or '--password' arguments. " +
            "Interactive username/password input to icloudpd is not supported"
      )
    }
  }
}
