package club.plutomc.talker.test.bukkit

import club.plutomc.talker.bukkit.BukkitTalkerBootstrap
import club.plutomc.talker.minecraft.SampleData
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        BukkitTalkerBootstrap.getInstance().registerListener(EasyListener)
        val commandMap =
            Bukkit.getServer().javaClass.getMethod("getCommandMap").invoke(Bukkit.getServer()) as CommandMap
        commandMap.register("talker", object : Command("talkerbukkit") {
            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
                val data = SampleData()
                data.add("name", "message")
                data.add("message", args.joinToString(" "))
                BukkitTalkerBootstrap.getInstance().send(data)
                return true
            }
        })
    }

}