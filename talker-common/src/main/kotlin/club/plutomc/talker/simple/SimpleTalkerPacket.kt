package club.plutomc.talker.simple

import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.TalkerReader
import club.plutomc.talker.api.TalkerWriter

class SimpleTalkerPacket : TalkerPacket {

    private var byteArray: ByteArray = byteArrayOf()

    fun toReader(): TalkerReader {
        return SimpleTalkerReader(byteArray)
    }

    fun fromWriter(writer: TalkerWriter) {
        this.byteArray = writer.toByteArray()
    }

    override fun write(writer: TalkerWriter) {
        writer.append(this.byteArray)
    }

}