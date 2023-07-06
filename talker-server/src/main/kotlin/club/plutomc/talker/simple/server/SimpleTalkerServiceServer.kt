package club.plutomc.talker.simple.server

import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.TalkerWriter
import club.plutomc.talker.api.server.TalkerServer
import club.plutomc.talker.api.server.TalkerServiceServer
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter

object SimpleTalkerServiceServer : TalkerServiceServer {

    override fun createServer(port: Int): TalkerServer {
        return SimpleTalkerServer(port)
    }

    override fun createPacket(serializationFunction: (TalkerWriter) -> Unit): TalkerPacket {
        val packet = SimpleTalkerPacket()
        val writer = SimpleTalkerWriter()
        writer.apply(serializationFunction)
        packet.fromWriter(writer)
        return packet
    }

}