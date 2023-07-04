package club.plutomc.talker.api

interface TalkerManager {

    fun registerSerializer(talkerSerializer: TalkerSerializer<Any>)

    fun registerDeserializer(talkerDeserializer: TalkerDeserializer<Any>)

    fun registerAdapter(talkerAdapter: TalkerAdapter<Any>) {
        this.registerSerializer(talkerAdapter)
        this.registerDeserializer(talkerAdapter)
    }

    fun registerReceiver(receiver: TalkerDataReceiver)

    fun <T> registerReceiver(type: Class<T>, receiver: TalkerDataBoundReceiver<T>)

    fun receive(context: TalkerContext, packet: TalkerPacket)

    fun listSerializers(): List<TalkerSerializer<out Any?>>

    fun listDeserializers(): List<TalkerDeserializer<out Any?>>

}