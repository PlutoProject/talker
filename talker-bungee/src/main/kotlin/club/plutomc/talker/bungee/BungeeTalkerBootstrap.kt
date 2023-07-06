package club.plutomc.talker.bungee

import club.plutomc.talker.api.TalkerService
import club.plutomc.talker.api.server.ClientConnection
import club.plutomc.talker.api.server.TalkerServer
import club.plutomc.talker.api.server.TalkerServiceServer
import club.plutomc.talker.minecraft.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

class BungeeTalkerBootstrap : Plugin(), SampleListenerProvider {

    private val gson = Gson()

    private lateinit var configFile: File
    private lateinit var server: TalkerServer
    private lateinit var config: Configuration

    private val listeners: MutableList<SampleListener> = mutableListOf()

    init {
        INSTANCE = this
    }

    override fun onLoad() {
        this.configFile = File(this.dataFolder, "config.yml")
        if (!this.configFile.exists()) {
            this.configFile.createNewFile()
            val defaultConfiguration = Configuration()
            defaultConfiguration["port"] = 37561
            ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(defaultConfiguration, this.configFile)
        }
        this.config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(this.configFile)
        this.server =
            (TalkerService.getService("server") as TalkerServiceServer).createServer(this.config.getInt("port"))
        this.server.getManager().registerReceiver(object : SampleReceiver(this) {
            override fun receive0(listener: SampleListener, event: SampleEvent) {
                ProxyServer.getInstance().scheduler.run {
                    listener.receive(event)
                }
            }

        })
    }

    override fun onEnable() {
        this.server.start()
    }

    override fun onDisable() {
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

        private lateinit var INSTANCE: BungeeTalkerBootstrap

        @JvmStatic
        fun getInstance(): BungeeTalkerBootstrap {
            return INSTANCE
        }

    }

}