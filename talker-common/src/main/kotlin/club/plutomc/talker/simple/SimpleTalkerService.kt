package club.plutomc.talker.simple

import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.TalkerService
import club.plutomc.talker.api.TalkerWriter

object SimpleTalkerService: TalkerService {

    override fun createPacket(serializationFunction: (TalkerWriter) -> Unit): TalkerPacket {
        val packet = SimpleTalkerPacket()
        val writer = SimpleTalkerWriter()
        writer.apply(serializationFunction)
        packet.fromWriter(writer)
        return packet
    }

}