package club.plutomc.talker.api

import java.nio.charset.Charset

interface TalkerWriter {

    fun writeByte(byte: Byte): TalkerWriter

    fun writeInt(int: Int): TalkerWriter

    fun writeShort(short: Short): TalkerWriter

    fun writeLong(long: Long): TalkerWriter

    fun writeFloat(float: Float): TalkerWriter

    fun writeDouble(double: Double): TalkerWriter

    fun writeString(string: String): TalkerWriter

    fun writeString(string: String, charset: Charset): TalkerWriter

    fun writeString(string: String, length: Int): TalkerWriter

    fun writeString(string: String, charset: Charset, length: Int): TalkerWriter

    fun append(byteArray: ByteArray): TalkerWriter

    fun append(reader: TalkerReader): TalkerWriter

    fun append(writer: TalkerWriter): TalkerWriter

    fun length(): Int

    /**
     * Remove the part over the specific length
     */
    fun limit(length: Int)

    /**
     * Fill the rest part with 0 if the length is lower than the specific length
     */
    fun fill(length: Int)

    fun toByteArray(): ByteArray

    fun toTypedArray(): Array<Byte>

}