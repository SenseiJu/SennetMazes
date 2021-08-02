package me.senseiju.sennetmazes.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sennetmazes.generators.chunk.VoidChunkGenerator
import me.senseiju.sennetmazes.service.ServiceProvider
import me.senseiju.sennetmazes.templates.TemplateService
import me.senseiju.sennetmazes.world.WorldService
import me.senseiju.sentils.extensions.MessageProvider
import me.senseiju.sentils.extensions.sendConfigMessage
import org.bukkit.Location
import org.bukkit.WorldCreator
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command("sennetmazes")
class SennetMazesCommand(private val plugin: SennetMazes) : CommandBase() {
    private val templateService = ServiceProvider.get<TemplateService>()
    private val worldService = ServiceProvider.get<WorldService>()

    @SubCommand("reload")
    fun reload(sender: CommandSender) {
        MessageProvider.messages.reload()
        ServiceProvider.reloadAll()

        plugin.config.reload()

        sender.sendConfigMessage("RELOAD-COMPLETE")
    }

    @SubCommand("generate")
    fun generate(sender: Player, templateName: String?, xSize: Int?, ySize: Int?) {
        templateName ?: return
        xSize ?: return
        ySize ?: return

        val template = templateService.getTemplate(templateName) ?: return

        worldService.generateNextMaze(sender, template, xSize, ySize)
    }
}