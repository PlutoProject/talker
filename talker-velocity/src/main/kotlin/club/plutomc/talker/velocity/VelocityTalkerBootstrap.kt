package club.plutomc.talker.velocity

import club.plutomc.talker.api.TalkerService
import club.plutomc.talker.api.server.ClientConnection
import club.plutomc.talker.api.server.TalkerServer
import club.plutomc.talker.api.server.TalkerServiceServer
import club.plutomc.talker.minecraft.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path

@Plugin(
    id = "talker",
    name = "Talker Velocity Bootstrap",
    version = "1.0-SNAPSHOT",
    description = "Talker server bootstrap for velocity proxy",
    authors = ["DeeChael"]
)
class VelocityTalkerBootstrap @Inject constructor(
    private val proxyServer: ProxyServer,
    private val logger: Logger,
    @DataDirectory private val dataDirectory: Path
) : SampleListenerProvider {

    private val gson = Gson()

    private val configFile: File
    private var config: JsonObject

    private val server: TalkerServer

    private val listeners: MutableList<SampleListener> = mutableListOf()

    init {
        INSTANCE = this
        this.configFile = File(this.dataDirectory.toFile(), "config.json")
        if (!this.configFile.exists()) {
            if (!this.dataDirectory.toFile().exists())
                this.dataDirectory.toFile().mkdirs()
            this.configFile.createNewFile()
            val writer = JsonWriter(FileWriter(this.configFile))
            writer.beginObject()
            writer.name("port")
            writer.value(37561)
            writer.endObject()
            writer.flush()
            writer.close()
        }
        val reader = JsonReader(FileReader(this.configFile))
        this.config = JsonParser.parseReader(reader).asJsonObject
        reader.close()

        this.server =
            (TalkerService.getService("server") as TalkerServiceServer).createServer(this.config["port"].asInt)
        this.server.getManager().registerReceiver(object : SampleReceiver(this) {
            override fun receive0(listener: SampleListener, event: SampleEvent) {
                this@VelocityTalkerBootstrap.proxyServer.scheduler.buildTask(this@VelocityTalkerBootstrap) {
                    listener.receive(event)
                }.schedule()
            }
        })
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        this.server.start()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        this.server.shutdown()
    }

    fun registerListener(listener: SampleListener) {
        if (this.listeners.contains(listener))
            throw IllegalAccessException("The listener has been registered")
        this.listeners.add(listener)
    }

    fun getServer(): TalkerServer {
        return this.server
    }

    fun send(data: SampleData) {
        this.server.send(TalkerService.getService("simple").createPacket { writer ->
            writer.writeByte(0)
            writer.writeByte(127)
            writer.writeByte(0)
            writer.writeByte(64)
            val jsonObject = JsonObject()
            for (entry in data.getAll().entries) {
                jsonObject.addProperty(entry.key, entry.value)
            }
            val message = this.gson.toJson(jsonObject)
            writer.writeInt(message.length)
            writer.writeString(message)
        })
    }

    fun send(data: SampleData, filter: (List<ClientConnection>) -> List<ClientConnection>) {
        this.server.send(TalkerService.getService("simple").createPacket { writer ->
            writer.writeByte(0)
            writer.writeByte(127)
            writer.writeByte(0)
            writer.writeByte(64)
            val jsonObject = JsonObject()
            for (entry in data.getAll().entries) {
                jsonObject.addProperty(entry.key, entry.value)
            }
            val message = this.gson.toJson(jsonObject)
            writer.writeInt(message.length)
            writer.writeString(message)
        }, filter)
    }

    override fun getListeners(): List<SampleListener> {
        return this.listeners.toList()
    }

    companion object {

        private lateinit var INSTANCE: VelocityTalkerBootstrap

        @JvmStatic
        fun getInstance(): VelocityTalkerBootstrap {
            return INSTANCE
        }

    }

}