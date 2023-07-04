package club.plutomc.talker.api

import java.lang.Exception

interface TalkerDataBoundReceiver<T> {

    fun receive(manager: TalkerManager, context: TalkerContext, rawPacket: TalkerPacket, packet: T)

    fun handleException(exception: Exception)

}