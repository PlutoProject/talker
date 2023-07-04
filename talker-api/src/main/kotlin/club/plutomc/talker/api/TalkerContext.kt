package club.plutomc.talker.api

import java.net.InetAddress

interface TalkerContext {

    fun getAddress(): InetAddress

    fun getPort(): Int

    fun send(packet: TalkerPacket)

    fun send(any: Any)

}