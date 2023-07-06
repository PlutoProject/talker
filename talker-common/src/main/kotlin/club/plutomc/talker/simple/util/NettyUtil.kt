package club.plutomc.talker.simple.util

import club.plutomc.talker.api.TalkerReader
import club.plutomc.talker.simple.SimpleTalkerReader
import io.netty.buffer.ByteBuf

object NettyUtil {

    fun castByteBufToTalkerReader(byteBuf: ByteBuf): TalkerReader {
        val byteList = mutableListOf<Byte>()
        while (byteBuf.isReadable) {
            byteList.add(byteBuf.readByte())
        }
        return SimpleTalkerReader(byteList.toByteArray())
    }

}