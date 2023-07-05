package club.plutomc.talker.test.bukkit

import club.plutomc.talker.minecraft.SampleEvent
import club.plutomc.talker.minecraft.SampleListener
import org.bukkit.Bukkit

object EasyListener: SampleListener {

    override fun receive(event: SampleEvent) {
        val data = event.data
        if (!data.contains("name"))
            return
        val name = data.get("name")
        if (name == "message") {
            if (!data.contains("message"))
                return
            val message = data.get("message")
            Bukkit.broadcastMessage(message)
            Bukkit.getConsoleSender().sendMessage("Receiver form proxy: " + message)
        }
    }

}