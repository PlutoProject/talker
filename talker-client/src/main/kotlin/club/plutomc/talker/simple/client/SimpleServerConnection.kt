package club.plutomc.talker.simple.client

import club.plutomc.talker.api.client.ServerConnection
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class SimpleServerConnection(private val client: SimpleTalkerClient, val channel: Channel): ServerConnection, ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg !is ByteBuf)
            return
        val first = msg.readByte()
        if (first == 1.toByte()) {
            val second = msg.readByte()
            if (second == 0.toByte()) {
                val writer = SimpleTalkerWriter()
                writer.writeByte(1)
                writer.writeByte(0)
                val bytes = this.client.getName().toByteArray(Charsets.UTF_8)
                writer.writeInt(bytes.size)
                writer.append(bytes)
            }
        } else if (first == 0.toByte()) {
            val packet = SimpleTalkerPacket()
            val writer = SimpleTalkerWriter()
            writer.append(msg.readBytes(msg.readableBytes()).array())
            packet.fromWriter(writer)
            this.client.getManager().receive(ClientTalkerContext(this.client, ctx), packet)
        }
    }

}