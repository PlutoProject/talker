package club.plutomc.talker.test.velocity

import club.plutomc.talker.minecraft.SampleData
import club.plutomc.talker.velocity.VelocityTalkerBootstrap
import com.google.inject.Inject
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.RawCommand
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "talker-test",
    name = "Talker Test",
    version = "1.0-SNAPSHOT",
    description = "Talker test for velocity proxy",
    authors = ["DeeChael"],
    dependencies = [Dependency(id="talker")]
)
class Main @Inject constructor(private val proxyServer: ProxyServer, private val logger: Logger, @DataDirectory private val dataDirectory: Path) {

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        VelocityTalkerBootstrap.getInstance().registerListener(EasyListener(this.proxyServer))
        val command = LiteralArgumentBuilder.literal<CommandSource>("talkervelocity")
            .executes {

                return@executes 1
            }
        val commandManager = this.proxyServer.commandManager
        commandManager.register(commandManager.metaBuilder("talkervelocity").build(), object : RawCommand {
            override fun execute(invocation: RawCommand.Invocation) {
                val data = SampleData()
                data.add("name", "message")
                data.add("message", invocation.arguments() ?: "from proxy")
                VelocityTalkerBootstrap.getInstance().send(data)
            }
        })
    }

}