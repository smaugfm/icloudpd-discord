package io.github.smaugfm.icloudpd.discord

class CircularBuffer(private val maxElements: Int) {
  private val buffer = ByteArray(maxElements);
  private var start = 0
  private var end = 0
  private var full = false

  val isFull get() = size == maxElements
  private val isEmpty get() = size == 0
  val string
    get() = String(buffer).let {
      if (isFull)
        it.substring(start) + it.substring(0, end)
      else
        it.substring(start, size)
    }

  fun add(byte: Byte) {

    if (isFull) {
      remove();
    }

    buffer[end++] = byte

    if (end >= maxElements) {
      end = 0
    }

    if (end == start) {
      full = true;
    }
  }

  private fun remove(): Byte? {
    if (isEmpty) {
      return null
    }

    val element = buffer[start++]

    if (start >= maxElements) {
      start = 0;
    }
    full = false;
    return element;
  }

  private val size: Int
    get() {
      return if (end < start) {
        maxElements - start + end
      } else if (end == start) {
        if (full) maxElements else 0
      } else {
        end - start;
      }
    }
}
