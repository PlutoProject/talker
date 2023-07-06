package club.plutomc.talker.simple.client

import club.plutomc.talker.api.TalkerManager
import club.plutomc.talker.api.TalkerPacket
import club.plutomc.talker.api.client.TalkerClient
import club.plutomc.talker.simple.SimpleTalkerManager
import club.plutomc.talker.simple.SimpleTalkerWriter
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.util.concurrent.Executors

class SimpleTalkerClient(private val name: String, private val address: InetAddress, private val port: Int) :
    TalkerClient {

    val logger = LoggerFactory.getLogger("Talker Client")

    private val executorService = Executors.newFixedThreadPool(4)

    private val nioEventLoopGroup: NioEventLoopGroup
    private val bootstrap: Bootstrap

    private lateinit var clientChannel: ChannelFuture
    private lateinit var serverConnection: SimpleServerConnection

    private val manager: TalkerManager = SimpleTalkerManager()

    private var started: Boolean = false
    private var shutdown: Boolean = false

    init {
        this.nioEventLoopGroup = NioEventLoopGroup()
        this.bootstrap = Bootstrap()
        this.bootstrap.group(this.nioEventLoopGroup)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(channel: SocketChannel) {
                    try {
                        val connection = SimpleServerConnection(this@SimpleTalkerClient, channel)
                        channel.pipeline().addLast(connection)
                        this@SimpleTalkerClient.serverConnection = connection
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
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
        this.executorService.execute {
            logger.info("Started client")
            this.clientChannel = this.bootstrap
                .connect(this.address, this.port)
                .sync()
            this.clientChannel.channel()
                .closeFuture()
                .sync()
            logger.info("Client closed")
        }
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