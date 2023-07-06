package club.plutomc.talker.api

interface TalkerDataBoundReceiver<T> {

    fun receive(manager: TalkerManager, context: TalkerContext, rawPacket: TalkerPacket, packet: T)

    fun handleException(exception: Exception)

}