package club.plutomc.talker.api.server

import club.plutomc.talker.api.TalkerPacket

interface ClientConnection {

    fun getName(): String

    fun send(packet: TalkerPacket)

}