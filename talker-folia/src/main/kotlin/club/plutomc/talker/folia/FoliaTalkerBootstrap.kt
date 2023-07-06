package club.plutomc.talker.folia

import club.plutomc.talker.api.TalkerService
import club.plutomc.talker.api.client.TalkerClient
import club.plutomc.talker.api.client.TalkerServiceClient
import club.plutomc.talker.minecraft.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.plugin.java.JavaPlugin
import java.net.Inet4Address

object FoliaTalkerBootstrap: JavaPlugin(), SampleListenerProvider {

    private val gson = Gson()

    private lateinit var client: TalkerClient

    private val listeners: MutableList<SampleListener> = mutableListOf()

    override fun onLoad() {
        this.saveDefaultConfig()
        this.reloadConfig()
        this.client = (TalkerService.getService("client") as TalkerServiceClient).createClient(
            this.config.getString(
                "name",
                ""
            )!!, Inet4Address.getByName(this.config.getString("host")), this.config.getInt("port")
        )
        this.client.getManager().registerReceiver(object : SampleReceiver(this) {
            override fun receive0(listener: SampleListener, event: SampleEvent) {
                listener.receive(event)
            }
        })
    }

    override fun onEnable() {
        this.client.start()
    }

    override fun onDisable() {
        this.client.shutdown()
    }

    @JvmStatic
    fun registerListener(listener: SampleListener) {
        if (this.listeners.contains(listener))
            throw IllegalAccessException("The listener has been registered")
        this.listeners.add(listener)
    }

    @JvmStatic
    fun getClient(): TalkerClient {
        return this.client
    }

    @JvmStatic
    fun send(data: SampleData) {
        this.client.send(TalkerService.getService("simple").createPacket { writer ->
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

    override fun getListeners(): List<SampleListener> {
        return this.listeners.toList()
    }

}