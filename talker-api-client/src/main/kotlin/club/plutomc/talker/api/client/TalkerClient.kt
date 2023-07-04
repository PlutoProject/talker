package club.plutomc.talker.api.client

import club.plutomc.talker.api.TalkerManager
import club.plutomc.talker.api.TalkerPacket
import java.net.InetAddress

interface TalkerClient {

    fun getServer(): InetAddress

    fun getPort(): Int

    fun send(packet: TalkerPacket)

    fun getManager(): TalkerManager

    fun getName(): String

    fun start()

    fun shutdown()

    fun isStarted(): Boolean

    fun isShutdown(): Boolean

}