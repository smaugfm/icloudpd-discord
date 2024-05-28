package io.github.smaugfm.icloudpd.discord

class StringSearchingCircularBuffer(private val searchString: String) {

  private val buffer = CircularBuffer(searchString.length)

  fun find(b: ByteArray, off: Int, len: Int): Boolean {
    var wasFound = false
    for (i in off until off + len) {
      if (advanceAndCheck(b[i]))
        wasFound = true
    }
    return wasFound
  }

  private fun advanceAndCheck(byte: Byte): Boolean {
    buffer.add(byte)

    if (buffer.isFull) {
      if (buffer.string == searchString)
        return true
    }
    return false
  }
}
