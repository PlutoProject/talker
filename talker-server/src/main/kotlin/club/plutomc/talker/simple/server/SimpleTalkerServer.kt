package club.plutomc.talker.simple.server

import club.plutomc.talker.api.TalkerManager
import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.server.ClientConnection
import club.plutomc.talker.api.server.TalkerServer
import club.plutomc.talker.simple.SimpleTalkerManager
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

class SimpleTalkerServer(private val port: Int): TalkerServer {

    private val bossGroup: EventLoopGroup
    private val workerGroup: EventLoopGroup
    private val serverBootstrap: ServerBootstrap

    private val channelFuture: ChannelFuture

    private val manager: TalkerManager = SimpleTalkerManager()

    private var started: Boolean = false
    private var shutdown: Boolean = false

    private val clients: MutableList<SimpleClientConnection> = mutableListOf()

    init {

        this.bossGroup = NioEventLoopGroup()
        this.workerGroup = NioEventLoopGroup();
        this.serverBootstrap = ServerBootstrap()
        this.serverBootstrap.group(this.bossGroup, this.workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(channel: SocketChannel) {
                    val connection = SimpleClientConnection(this@SimpleTalkerServer, "", channel)
                    channel.pipeline()
                        .addLast(connection)
                }
            })
            this.channelFuture = this.serverBootstrap.bind(this.port)
    }

    override fun listClients(): List<SimpleClientConnection> {
        return this.clients.toList()
    }

    override fun send(packet: TalkerPacket) {
        val writer = SimpleTalkerWriter()
        packet.write(writer)
        val wrapped = mutableListOf<Byte>(0)
        wrapped.addAll(listOf(*writer.toTypedArray()))
        val byteBuf = Unpooled.wrappedBuffer(writer.toByteArray())
        for (connection in this.clients) {
            connection.channel.writeAndFlush(byteBuf)
        }
    }

    override fun send(packet: TalkerPacket, filter: (List<ClientConnection>) -> List<ClientConnection>) {
        val writer = SimpleTalkerWriter()
        packet.write(writer)
        val wrapped = mutableListOf<Byte>(0)
        wrapped.addAll(listOf(*writer.toTypedArray()))
        val byteBuf = Unpooled.wrappedBuffer(writer.toByteArray())

        val list = filter(this.listClients())
        for (connection in list) {
            if (connection !is SimpleClientConnection)
                continue
            connection.channel.writeAndFlush(byteBuf)
        }
    }

    override fun getManager(): TalkerManager {
        return this.manager
    }

    override fun getPort(): Int {
        return this.port
    }

    override fun start() {
        if (this.started)
            throw IllegalAccessException("The client has started")
        this.started = true
        this.channelFuture.sync()
        this.channelFuture.channel()
            .closeFuture()
            .sync()
    }

    override fun shutdown() {
        if (!this.started)
            throw IllegalAccessException("The client hasn't started")
        if (this.shutdown)
            throw IllegalAccessException("The client has shutdown")
        this.channelFuture.cancel(true)
        this.bossGroup.shutdownGracefully()
        this.workerGroup.shutdownGracefully()
        this.shutdown = true
    }

    override fun isStarted(): Boolean {
        return this.started
    }

    override fun isShutdown(): Boolean {
        return this.shutdown
    }

}