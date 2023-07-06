package club.plutomc.talker.api.server

import club.plutomc.talker.api.TalkerManager
import club.plutomc.talker.api.TalkerPacket

interface TalkerServer {

    fun listClients(): List<ClientConnection>

    fun send(packet: TalkerPacket)

    fun send(packet: TalkerPacket, filter: (List<ClientConnection>) -> List<ClientConnection>)

    fun getManager(): TalkerManager

    fun getPort(): Int

    fun disconnect(client: ClientConnection)

    fun start()

    fun shutdown()

    fun isStarted(): Boolean

    fun isShutdown(): Boolean

}