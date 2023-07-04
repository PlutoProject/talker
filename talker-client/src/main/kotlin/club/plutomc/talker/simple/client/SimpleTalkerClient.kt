package club.plutomc.talker.simple.client

import club.plutomc.talker.api.TalkerManager
import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.client.ServerConnection
import club.plutomc.talker.api.client.TalkerClient
import club.plutomc.talker.simple.SimpleTalkerManager
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetAddress

class SimpleTalkerClient(private val name: String, private val address: InetAddress, private val port: Int): TalkerClient {

    private val nioEventLoopGroup: NioEventLoopGroup
    private val bootstrap: Bootstrap

    private var clientChannel: ChannelFuture
    private lateinit var serverConnection: SimpleServerConnection

    private val manager: TalkerManager = SimpleTalkerManager()

    private var started: Boolean = false
    private var shutdown: Boolean = false

    init {
        this.nioEventLoopGroup = NioEventLoopGroup()
        this.bootstrap = Bootstrap()
        this.bootstrap.group(this.nioEventLoopGroup)
            .channel(NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(channel: SocketChannel) {
                    this@SimpleTalkerClient.serverConnection = SimpleServerConnection(this@SimpleTalkerClient, channel)
                }
            })
        this.clientChannel = this.bootstrap.connect(this.address, this.port)
    }

    override fun getServer(): InetAddress {
        return this.address
    }

    override fun getPort(): Int {
        return this.port
    }

    override fun send(packet: TalkerPacket) {
        if (!this.started)
            throw IllegalAccessException("The client hasn't started")
        if (this.shutdown)
            throw IllegalAccessException("The client has shutdown")
        val writer = SimpleTalkerWriter()
        packet.write(writer)
        val wrapped = mutableListOf<Byte>(0)
        wrapped.addAll(listOf(*writer.toTypedArray()))
        val byteBuf = Unpooled.wrappedBuffer(writer.toByteArray())

        this.serverConnection.channel.writeAndFlush(byteBuf)
    }

    override fun getManager(): TalkerManager {
        return this.manager
    }

    override fun getName(): String {
        return this.name
    }

    override fun start() {
        if (this.started)
            throw IllegalAccessException("The client has started")
        this.started = true
        this.clientChannel.sync()
        this.clientChannel.channel()
            .closeFuture()
            .sync()
    }

    override fun shutdown() {
        if (!this.started)
            throw IllegalAccessException("The client hasn't started")
        if (this.shutdown)
            throw IllegalAccessException("The client has shutdown")
        this.clientChannel.cancel(true)
        this.nioEventLoopGroup.shutdownGracefully()
        this.shutdown = true
    }

    override fun isStarted(): Boolean {
        return this.started
    }

    override fun isShutdown(): Boolean {
        return this.shutdown
    }

}