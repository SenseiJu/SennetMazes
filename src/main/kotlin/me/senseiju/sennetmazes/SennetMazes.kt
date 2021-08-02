package me.senseiju.sennetmazes

import me.mattstudios.mf.base.CommandManager
import me.senseiju.sennetmazes.commands.SennetMazesCommand
import me.senseiju.sennetmazes.service.ServiceProvider
import me.senseiju.sennetmazes.templates.TemplateService
import me.senseiju.sennetmazes.world.WorldService
import me.senseiju.sentils.extensions.MessageProvider
import me.senseiju.sentils.storage.ConfigFile
import org.bukkit.plugin.java.JavaPlugin

class SennetMazes : JavaPlugin() {
    private lateinit var commandManager: CommandManager

    val config = ConfigFile(this, "config.yml", true)

    init {
        MessageProvider.messages = ConfigFile(this, "messages.yml", true)
    }

    override fun onEnable() {
        ServiceProvider.add(
            TemplateService(this),
            WorldService(this)
        )

        commandManager = CommandManager(this)
        commandManager.register(
            SennetMazesCommand(this)
        )
    }

    override fun onDisable() {
        ServiceProvider.get<WorldService>().deleteWorld()

        ServiceProvider.disableAll()
    }
}