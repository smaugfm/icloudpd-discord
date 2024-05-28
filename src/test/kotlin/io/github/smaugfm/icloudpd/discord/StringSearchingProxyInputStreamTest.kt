package io.github.smaugfm.icloudpd.discord

import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringSearchingProxyInputStreamTest {
  @Test
  fun one() {
    val searchText = "vasa"
    val (os, i, found) = perform(searchText)
    os.write('k'.code)
    i.read()
    assertFalse { found() }
    os.write('v'.code)
    i.read()
    assertFalse { found() }
    os.write('a'.code)
    i.read()
    assertFalse { found() }
    os.write('s'.code)
    i.read()
    assertFalse { found() }
    os.write('a'.code)
    i.read()
    assertTrue { found() }
    os.write('a'.code)
    i.read()
  }

  @Test
  fun two() {
    val searchText = "vasa"
    val (os, i, found) = perform(searchText)
    os.write("vasa".toByteArray())
    i.readNBytes(4)
    assertTrue { found() }
  }

  @Test
  fun three() {
    val searchText = "vasa"
    val (os, i, found) = perform(searchText)
    os.write("vas vasa".toByteArray())
    i.readNBytes(8)
    assertTrue { found() }
  }

  private fun perform(searchText: String): Triple<PipedOutputStream, StringSearchingCircularBuffer, () -> Boolean> {
    val pipedInputStream = PipedInputStream()
    val pipedOutputStream = PipedOutputStream(pipedInputStream)
    var found = false
    val inputStream = StringSearchingCircularBuffer(pipedInputStream, searchText) {
      found = true
    }
    return Triple(pipedOutputStream, inputStream) { found }
  }
}
