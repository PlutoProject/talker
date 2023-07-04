package club.plutomc.talker.simple

import club.plutomc.talker.api.*
import java.lang.reflect.ParameterizedType

class SimpleTalkerManager: TalkerManager {

    private val serializers: MutableList<TalkerSerializer<Any>> = mutableListOf()
    private val deserializers: MutableList<TalkerDeserializer<Any>> = mutableListOf()

    private val receivers: MutableList<TalkerDataReceiver> = mutableListOf()
    private val boundReceivers: MutableList<TalkerDataBoundReceiver<out Any?>> = mutableListOf()

    override fun registerSerializer(talkerSerializer: TalkerSerializer<Any>) {
        if (this.serializers.contains(talkerSerializer))
            throw IllegalAccessException("The serializer has been registered")
        this.serializers.add(talkerSerializer)
    }

    override fun registerDeserializer(talkerDeserializer: TalkerDeserializer<Any>) {
        if (this.deserializers.contains(talkerDeserializer))
            throw IllegalAccessException("The deserializer has been registered")
        this.deserializers.add(talkerDeserializer)
    }

    override fun registerReceiver(receiver: TalkerDataReceiver) {
        if (this.receivers.contains(receiver))
            throw IllegalAccessException("The receiver has been registered")
        this.receivers.add(receiver)
    }

    override fun <T> registerReceiver(type: Class<T>, receiver: TalkerDataBoundReceiver<T>) {
        if (this.boundReceivers.contains(receiver))
            throw IllegalAccessException("The receiver has been registered")
        this.boundReceivers.add(receiver)
    }

    override fun receive(context: TalkerContext, packet: TalkerPacket) {
        val writer = SimpleTalkerWriter()
        packet.write(writer)
        val reader = SimpleTalkerReader(writer.toByteArray())
        var any: Any? = null
        for (serializer in this.serializers) {
            reader.reset()
            any = serializer.serialize(reader)
            if (any != null)
                break
        }
        if (any != null) {
            val boundReceivers = mutableListOf<TalkerDataBoundReceiver<out Any?>>()
            for (boundReceiver in this.boundReceivers) {
                val anonymous = object {
                    val receiver = boundReceiver
                }
                val field = anonymous.javaClass.getDeclaredField("receiver")
                field.trySetAccessible()
                val genericType = field.genericType as ParameterizedType
                if ((genericType.actualTypeArguments[0] as Class<*>).isInstance(any))
                    boundReceivers.add(boundReceiver)
            }
            for (boundReceiver in boundReceivers) {
                boundReceiver.receive(this, context, packet, any as Nothing)
            }
            for (receiver in this.receivers) {
                reader.reset()
                receiver.receive(this, context, packet, reader)
            }
        }
    }

    override fun listSerializers(): List<TalkerSerializer<out Any?>> {
        return this.serializers.toList()
    }

    override fun listDeserializers(): List<TalkerDeserializer<out Any?>> {
        return this.deserializers.toList()
    }

}