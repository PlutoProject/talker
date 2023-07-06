package club.plutomc.talker.simple.client

import club.plutomc.talker.api.client.ServerConnection
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import club.plutomc.talker.simple.util.NettyUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class SimpleServerConnection(private val client: SimpleTalkerClient, val channel: Channel) : ServerConnection,
    ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        this.client.logger.info("Client connected successfully")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        this.client.logger.info("Client has disconnected with the server")
        if (!this.client.isShutdown())
            this.client.shutdown()
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        msg as ByteBuf
        val reader = NettyUtil.castByteBufToTalkerReader(msg)
        val first = reader.readByte()
        if (first == 1.toByte()) {
            val second = reader.readByte()
            if (second == 0.toByte()) {
                val writer = SimpleTalkerWriter()
                writer.writeByte(1)
                writer.writeByte(0)
                val bytes = this.client.getName().toByteArray(Charsets.UTF_8)
                writer.writeInt(bytes.size)
                writer.append(bytes)
                val byteBuf = Unpooled.wrappedBuffer(writer.toByteArray())
                ctx.channel().writeAndFlush(byteBuf)
            }
        } else if (first == 0.toByte()) {
            val packet = SimpleTalkerPacket()
            val writer = SimpleTalkerWriter()
            writer.append(reader.readBytes(reader.availableLength()))
            packet.fromWriter(writer)
            this.client.getManager().receive(ClientTalkerContext(this.client, ctx), packet)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
    }

}