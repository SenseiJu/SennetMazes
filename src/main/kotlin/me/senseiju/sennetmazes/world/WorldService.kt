package me.senseiju.sennetmazes.world

import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sennetmazes.generators.chunk.VoidChunkGenerator
import me.senseiju.sennetmazes.generators.maze.generateSegmentMappedMaze
import me.senseiju.sennetmazes.service.Service
import me.senseiju.sennetmazes.templates.Template
import me.senseiju.sennetmazes.templates.TemplatePaster
import me.senseiju.sentils.extensions.sendConfigMessage
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import java.io.File
import java.util.*

class WorldService(private val plugin: SennetMazes) : Service() {
    private val worldContainerPath = plugin.server.worldContainer.absolutePath.removeSuffix("\\.")
    private val mazeWorld = createWorld()

    private var nextLocation = Location(mazeWorld, 0.0, 5.0, 0.0)

    fun generateNextMaze(player: Player, template: Template, x: Int, y: Int) {
        with (nextLocation.clone()) {
            TemplatePaster(
                plugin,
                template,
                generateSegmentMappedMaze(x, y),
                this
            ) {
                player.teleport(template.calculatePlayerSpawn(this))
            }
        }

        nextLocation.x = (template.sizeWithDepth * x) + 5.0
    }

    fun deleteWorld() {
        mazeWorld.players.forEach {
            it.teleport(Location(plugin.server.getWorld("world"), 0.0, 100.0, 0.0))
        }
        plugin.server.unloadWorld(mazeWorld, false)

        File(worldContainerPath, mazeWorld.name).deleteRecursively()
    }

    private fun createWorld(): World {
        val world = WorldCreator("maze").generator(VoidChunkGenerator()).createWorld()
            ?: throw WorldLoadException("maze")

        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)

        return world
    }
}

private class WorldLoadException(world: String) : Exception() {
    override val message = "Failed to create world '$world'"
}