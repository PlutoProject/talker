package club.plutomc.talker.simple.server

import club.plutomc.talker.api.TalkerContext
import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.lang.reflect.ParameterizedType
import java.net.InetAddress
import java.net.InetSocketAddress

class ServerTalkerContext(private val server: SimpleTalkerServer, private val context: ChannelHandlerContext): TalkerContext {

    override fun getAddress(): InetAddress {
        val remote = context.channel().remoteAddress()
        if (remote !is InetSocketAddress)
            return InetAddress.getLocalHost()
        return remote.address
    }

    override fun getPort(): Int {
        val remote = context.channel().remoteAddress()
        if (remote !is InetSocketAddress)
            return this.server.getPort()
        return remote.port
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
        for (deserializer in this.server.getManager().listDeserializers()) {
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