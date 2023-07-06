package club.plutomc.talker.simple

import club.plutomc.talker.api.TalkerReader
import club.plutomc.talker.api.TalkerWriter
import java.nio.ByteBuffer
import java.nio.charset.Charset

class SimpleTalkerWriter : TalkerWriter {

    private val content: MutableList<Byte> = mutableListOf()

    override fun writeByte(byte: Byte): TalkerWriter {
        this.content.add(byte)
        return this
    }

    override fun writeInt(int: Int): TalkerWriter {
        this.append(ByteBuffer.allocate(4).putInt(int).array())
        return this
    }

    override fun writeShort(short: Short): TalkerWriter {
        this.append(ByteBuffer.allocate(2).putShort(short).array())
        return this
    }

    override fun writeLong(long: Long): TalkerWriter {
        this.append(ByteBuffer.allocate(8).putLong(long).array())
        return this
    }

    override fun writeFloat(float: Float): TalkerWriter {
        this.append(ByteBuffer.allocate(4).putFloat(float).array())
        return this
    }

    override fun writeDouble(double: Double): TalkerWriter {
        this.append(ByteBuffer.allocate(8).putDouble(double).array())
        return this
    }

    override fun writeString(string: String): TalkerWriter {
        return this.writeString(string, Charsets.UTF_8, string.length)
    }

    override fun writeString(string: String, charset: Charset): TalkerWriter {
        return this.writeString(string, charset, string.length)
    }

    override fun writeString(string: String, length: Int): TalkerWriter {
        return this.writeString(string, Charsets.UTF_8, length)
    }

    override fun writeString(string: String, charset: Charset, length: Int): TalkerWriter {
        if (length <= 0)
            return this
        val resultString: String
        if (length < string.length)
            resultString = string.substring(0, length)
        else
            resultString = string
        for (byte in resultString.toByteArray(charset)) {
            this.content.add(byte)
        }
        return this
    }

    override fun append(byteArray: ByteArray): TalkerWriter {
        for (byte in byteArray) {
            this.content.add(byte)
        }
        return this
    }

    override fun append(reader: TalkerReader): TalkerWriter {
        for (byte in reader.toByteArray()) {
            this.content.add(byte)
        }
        return this
    }

    override fun append(writer: TalkerWriter): TalkerWriter {
        this.append(writer.toByteArray())
        return this
    }

    override fun length(): Int {
        return this.content.size
    }

    override fun limit(length: Int) {
        if (this.content.size <= length)
            return
        val mutableList = mutableListOf<Byte>()
        for (i in 0 until length) {
            mutableList.add(this.content[i])
        }
        this.content.clear()
        this.content.addAll(mutableList)
    }

    override fun fill(length: Int) {
        if (this.content.size >= length)
            return
        for (i in 0 until (length - this.content.size)) {
            this.content.add(0)
        }
    }

    override fun toByteArray(): ByteArray {
        return this.content.toByteArray()
    }

    override fun toTypedArray(): Array<Byte> {
        return this.content.toTypedArray()
    }

}