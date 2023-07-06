package club.plutomc.talker.bukkit

import club.plutomc.talker.api.TalkerService
import club.plutomc.talker.api.client.TalkerClient
import club.plutomc.talker.api.client.TalkerServiceClient
import club.plutomc.talker.minecraft.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.net.Inet4Address

class BukkitTalkerBootstrap : JavaPlugin(), SampleListenerProvider {

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
                object : BukkitRunnable() {
                    override fun run() {
                        listener.receive(event)
                    }
                }.runTask(this@BukkitTalkerBootstrap)
            }
        })
    }

    override fun onEnable() {
        this.client.start()
    }

    override fun onDisable() {
        this.client.shutdown()
    }

    fun registerListener(listener: SampleListener) {
        if (this.listeners.contains(listener))
            throw IllegalAccessException("The listener has been registered")
        this.listeners.add(listener)
    }

    fun getClient(): TalkerClient {
        return this.client
    }

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

    companion object {

        @JvmStatic
        fun getInstance(): BukkitTalkerBootstrap {
            return getPlugin(BukkitTalkerBootstrap::class.java)
        }

    }

}