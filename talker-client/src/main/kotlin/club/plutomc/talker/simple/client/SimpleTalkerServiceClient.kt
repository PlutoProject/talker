package club.plutomc.talker.simple.client

import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.TalkerWriter
import club.plutomc.talker.api.client.TalkerClient
import club.plutomc.talker.api.client.TalkerServiceClient
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import java.net.InetAddress

object SimpleTalkerServiceClient : TalkerServiceClient {

    override fun createClient(name: String, address: InetAddress, port: Int): TalkerClient {
        return SimpleTalkerClient(name, address, port)
    }

    override fun createPacket(serializationFunction: (TalkerWriter) -> Unit): TalkerPacket {
        val packet = SimpleTalkerPacket()
        val writer = SimpleTalkerWriter()
        writer.apply(serializationFunction)
        packet.fromWriter(writer)
        return packet
    }

}