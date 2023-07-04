package club.plutomc.talker.simple.server

import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.server.ClientConnection
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class SimpleClientConnection(private val server: SimpleTalkerServer, private var name: String, val channel: Channel): ClientConnection,
    ChannelInboundHandlerAdapter() {

    override fun getName(): String {
        return this.name
    }

    override fun send(packet: TalkerPacket) {
        val writer = SimpleTalkerWriter()
        packet.write(writer)
        val wrapped = mutableListOf<Byte>(0)
        wrapped.addAll(listOf(*writer.toTypedArray()))
        val byteBuf = Unpooled.wrappedBuffer(writer.toByteArray())
        this.channel.writeAndFlush(byteBuf)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        val writer = SimpleTalkerWriter()
        writer.writeByte(1)
        writer.writeByte(0)
        ctx.channel().writeAndFlush(Unpooled.wrappedBuffer(writer.toByteArray()))
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ByteBuf)
            return
        val first = msg.readByte()
        if (first == 1.toByte()) {
            val second = msg.readByte()
            if (second == 0.toByte()) {
                val length = msg.readInt()
                val name = String(msg.readBytes(length).array(), Charsets.UTF_8)
                this.name = name
            }
        } else if (first == 0.toByte()) {
            val packet = SimpleTalkerPacket()
            val writer = SimpleTalkerWriter()
            writer.append(msg.readBytes(msg.readableBytes()).array())
            packet.fromWriter(writer)
            this.server.getManager().receive(ServerTalkerContext(this.server, ctx), packet)
        }

    }

}