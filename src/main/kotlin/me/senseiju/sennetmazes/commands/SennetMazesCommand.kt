package me.senseiju.sennetmazes.commands

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sennetmazes.generators.SegmentMappedMazeGenerator
import me.senseiju.sennetmazes.service.ServiceProvider
import me.senseiju.sennetmazes.templates.TemplateService
import me.senseiju.sentils.extensions.MessageProvider
import me.senseiju.sentils.extensions.sendConfigMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Command("sennetmazes")
class SennetMazesCommand(private val plugin: SennetMazes) : CommandBase() {
    private val templateService = ServiceProvider.get<TemplateService>()

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
        val location = sender.location

        template.pasteMaze(
            SegmentMappedMazeGenerator(xSize, ySize).mapMazeToSegments(),
            BukkitAdapter.adapt(sender.world),
            BlockVector3.at(location.x, location.y, location.z)
        ) {
            println("finished generating maze")
        }
    }
}