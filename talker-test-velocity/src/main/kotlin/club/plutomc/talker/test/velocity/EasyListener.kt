package club.plutomc.talker.test.velocity

import club.plutomc.talker.minecraft.SampleEvent
import club.plutomc.talker.minecraft.SampleListener
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component

class EasyListener(val server: ProxyServer) : SampleListener {

    override fun receive(event: SampleEvent) {
        val data = event.data
        if (!data.contains("name"))
            return
        val name = data.get("name")
        if (name == "message") {
            if (!data.contains("message"))
                return
            val message = data.get("message")
            server.consoleCommandSource.sendMessage(Component.text(String.format("Message from server: %s", message)))
        }
    }

}