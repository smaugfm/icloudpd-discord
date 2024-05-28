package io.github.smaugfm.icloudpd.discord

import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {
  val wrapper = IcloudPdBinaryAuthListeningWrapper {
    BufferedReader(InputStreamReader(System.`in`)).use {
      it.readLine()
    }
  }
  wrapper.execAndBlock(args)
}

