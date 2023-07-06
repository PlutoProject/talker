package club.plutomc.talker.simple.client

import club.plutomc.talker.api.TalkerContext
import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.lang.reflect.ParameterizedType
import java.net.InetAddress

class ClientTalkerContext(private val client: SimpleTalkerClient, private val context: ChannelHandlerContext) :
    TalkerContext {

    override fun getAddress(): InetAddress {
        return this.client.getServer()
    }

    override fun getPort(): Int {
        return this.client.getPort()
    }

    override fun send(packet: TalkerPacket) {
        val writer = SimpleTalkerWriter()
        packet.write(writer)
        val wrapped = mutableListOf<Byte>(0)
        wrapped.addAll(listOf(*writer.toTypedArray()))
        val byteBuf = Unpooled.wrappedBuffer(writer.toByteArray())
        this.context.writeAndFlush(byteBuf)
    }

    @Suppress("UNREACHABLE_CODE")
    override fun send(any: Any) {
        for (deserializer in this.client.getManager().listDeserializers()) {
            val anonymous = object {
                val deserializer = deserializer
            }
            val field = anonymous.javaClass.getDeclaredField("receiver")
            field.trySetAccessible()
            val genericType = field.genericType as ParameterizedType
            if ((genericType.actualTypeArguments[0] as Class<*>).isInstance(any)) {
                val writer = SimpleTalkerWriter()
                deserializer.deserialize(any as Nothing, writer)
                val packet = SimpleTalkerPacket()
                packet.fromWriter(writer)
                this.send(packet)
                return
            }
        }
    }

}