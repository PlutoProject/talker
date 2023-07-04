package club.plutomc.talker.api

import java.nio.charset.Charset

interface TalkerReader {

    fun readByte(): Byte

    fun readInt(): Int

    fun readShort(): Short

    fun readLong(): Long

    fun readFloat(): Float

    fun readDouble(): Double

    fun readString(length: Int): String

    fun readString(charset: Charset, length: Int): String

    fun skip(length: Int)

    fun totalLength(): Int

    fun availableLength(): Int

    fun toByteArray(): ByteArray

    fun toTypedArray(): Array<Byte>

    fun reset()

}