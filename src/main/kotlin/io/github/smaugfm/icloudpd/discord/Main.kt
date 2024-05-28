package io.github.smaugfm.icloudpd.discord

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.await
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
  val token = getEnv("BOT_TOKEN")
  val discordUserId = getEnv("DISCORD_USERID")

  logger.info { "Creating Discord bot..." }
  val jda = light(token, true) { intents += GatewayIntent.DIRECT_MESSAGES }
  jda.awaitReady()
  logger.info { "Starting icloudpd..." }

  runBlocking {
    val channel = jda.openPrivateChannelById(discordUserId).await()
    val icloudPdWrapper = IcloudPdBinaryAuthListeningWrapper {
      channel.sendMessage(
        "Please send two-factor authentication code from the prompt on your Apple devices"
      ).await()

      waitFor2FaCode(jda, discordUserId, channel)
    }

    while (true) {
      val exitValue = icloudPdWrapper.execAndBlock(args)
      logger.error { "icloudpd exited with value $exitValue. Restarting..." }
    }
  }
}

private suspend fun waitFor2FaCode(
  jda: JDA, discordUserId: String, channel: PrivateChannel
): String {
  while (true) {
    val text = jda.await<MessageReceivedEvent> { it.message.author.id == discordUserId }
      .message.contentDisplay
    val code = text
      .trim()
      .replace("\\s+", "").toLongOrNull()
    if (code == null) {
      channel.sendMessage("2FA code must be a 6-digit number").await()
    } else {
      return code.toString()
    }
  }
}

private fun getEnv(name: String) = System.getenv()[name].also {
  if (it.isNullOrBlank()) {
    fail("$name is not set")
  }
}!!

fun fail(message: String): Nothing {
  logger.error { message }
  exitProcess(1)
}

