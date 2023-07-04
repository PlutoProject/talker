package club.plutomc.talker.api

import java.lang.Exception

interface TalkerDataReceiver {

    fun receive(manager: TalkerManager, context: TalkerContext, rawPacket: TalkerPacket, packet: Any)

    fun handleException(exception: Exception)

}