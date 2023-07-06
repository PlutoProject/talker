package club.plutomc.talker.simple.server

import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.server.ClientConnection
import club.plutomc.talker.simple.SimpleTalkerPacket
import club.plutomc.talker.simple.SimpleTalkerWriter
import club.plutomc.talker.simple.util.NettyUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class SimpleClientConnection(private val server: SimpleTalkerServer, private var name: String, val channel: Channel) :
    ClientConnection,
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
        this.server.logger.info("A client connected successfully, trying to fetch its name")
        val writer = SimpleTalkerWriter()
        writer.writeByte(1)
        writer.writeByte(0)
        this.channel.writeAndFlush(Unpooled.wrappedBuffer(writer.toByteArray()))
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        this.server.logger.info("Client ${this.name} disconnected")
        this.server.disconnect(this)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        msg as ByteBuf
        val reader = NettyUtil.castByteBufToTalkerReader(msg)
        val first = reader.readByte()
        if (first == 1.toByte()) {
            val second = reader.readByte()
            if (second == 0.toByte()) {
                val length = reader.readInt()
                val name = String(reader.readBytes(length), Charsets.UTF_8)
                this.name = name
                this.server.logger.info("Client $name connected")
            }
        } else if (first == 0.toByte()) {
            val packet = SimpleTalkerPacket()
            val writer = SimpleTalkerWriter()
            writer.append(reader.readBytes(reader.availableLength()))
            packet.fromWriter(writer)
            this.server.getManager().receive(ServerTalkerContext(this.server, ctx), packet)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
    }

}