package io.github.smaugfm.icloudpd.discord

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringSearchingProxyInputStreamTest {
  @Test
  fun one() {
    val buffer = StringSearchingCircularBuffer("vasa")
    assertFalse(buffer.feedAndFind("k"))
    assertFalse(buffer.feedAndFind("v"))
    assertFalse(buffer.feedAndFind("a"))
    assertFalse(buffer.feedAndFind("s"))
    assertTrue(buffer.feedAndFind("a"))
    assertFalse(buffer.feedAndFind("a"))
  }

  @Test
  fun two() {
    val buffer = StringSearchingCircularBuffer("vasa")
    assertTrue(buffer.feedAndFind("vasa"))
  }

  @Test
  fun three() {
    val buffer = StringSearchingCircularBuffer("vasa")
    assertTrue(buffer.feedAndFind("vas vasa"))
  }

  private fun StringSearchingCircularBuffer.feedAndFind(str: String): Boolean =
    feedAndFind(str.toByteArray(), 0, str.length)
}
