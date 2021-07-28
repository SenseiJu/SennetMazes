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

    val activeWorlds = arrayListOf<World>()

    override fun onDisable() {
        activeWorlds.forEach {
            deleteWorld(it)
        }
    }

    override fun onReload() {

    }

    fun generateMazeWorld(player: Player, template: Template, x: Int, y: Int) {
        val world = createWorld()
        if (world == null) {
            player.sendConfigMessage("WORLD-GENERATION-FAILED")
            return
        }

        val startLocation = Location(world, 0.0, 5.0, 0.0)
        TemplatePaster(
            template,
            generateSegmentMappedMaze(x, y),
            startLocation
        ) {
            player.teleport(template.calculatePlayerSpawn(startLocation))
        }.paste()

        activeWorlds.add(world)
    }

    private fun createWorld(): World? {
        val world = WorldCreator("mazes/${UUID.randomUUID()}").generator(VoidChunkGenerator()).createWorld() ?: return null

        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)

        return world
    }

    private fun deleteWorld(world: World) {
        world.players.forEach {
            it.teleport(Location(plugin.server.getWorld("world"), 0.0, 100.0, 0.0))
        }
        plugin.server.unloadWorld(world, false)

        File(worldContainerPath, world.name).deleteRecursively()
    }
}