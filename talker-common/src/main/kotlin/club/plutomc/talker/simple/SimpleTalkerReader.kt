package club.plutomc.talker.simple

import club.plutomc.talker.api.TalkerReader
import java.nio.ByteBuffer
import java.nio.charset.Charset

class SimpleTalkerReader(val content: ByteArray) : TalkerReader {

    private var offset: Int = 0

    override fun readByte(): Byte {
        return readBytes(1)[0]
    }

    override fun readInt(): Int {
        val bytes = readBytes(4)
        return ByteBuffer.wrap(bytes).getInt()
    }

    override fun readShort(): Short {
        val bytes = readBytes(2)
        return ByteBuffer.wrap(bytes).getShort()
    }

    override fun readLong(): Long {
        val bytes = readBytes(8)
        return ByteBuffer.wrap(bytes).getLong()
    }

    override fun readFloat(): Float {
        val bytes = readBytes(4)
        return ByteBuffer.wrap(bytes).getFloat()
    }

    override fun readDouble(): Double {
        val bytes = readBytes(8)
        return ByteBuffer.wrap(bytes).getDouble()
    }

    override fun readString(length: Int): String {
        return this.readString(Charsets.UTF_8, length)
    }

    override fun readString(charset: Charset, length: Int): String {
        if (length > this.availableLength())
            throw ArrayIndexOutOfBoundsException()
        return String(readBytes(length), charset)
    }

    override fun skip(length: Int) {
        if (this.availableLength() < length)
            throw ArrayIndexOutOfBoundsException()
        this.offset += length
    }

    private fun readBytes(length: Int): ByteArray {
        val byteList = mutableListOf<Byte>()
        for (i in 0 until length) {
            byteList.add(this.content[this.offset])
            this.offset += 1
        }
        return byteList.toByteArray()
    }

    override fun totalLength(): Int {
        return this.content.size
    }

    override fun availableLength(): Int {
        return this.content.size - offset
    }

    override fun toByteArray(): ByteArray {
        return this.content.clone()
    }

    override fun toTypedArray(): Array<Byte> {
        val list = mutableListOf<Byte>()
        for (byte in this.content)
            list.add(byte)
        return list.toTypedArray()
    }

    override fun reset() {
        this.offset = 0
    }

}