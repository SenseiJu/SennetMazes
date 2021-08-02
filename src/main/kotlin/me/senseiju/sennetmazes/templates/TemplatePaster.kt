package me.senseiju.sennetmazes.templates

import com.fastasyncworldedit.core.FaweAPI
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sentils.runnables.newRunnable
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable
import kotlin.time.Duration

private val taskManager = FaweAPI.getTaskManager()

class TemplatePaster(
    plugin: SennetMazes,
    private val template: Template,
    private val mappedMaze: List<List<Segment>>,
    startLocation: Location,
    private val onFinish: () -> Unit
) : BukkitRunnable() {
    private val startTime = System.currentTimeMillis()
    private val world = BukkitAdapter.adapt(startLocation.world)
    private val startingVector = BlockVector3.at(startLocation.x, startLocation.y, startLocation.z)

    private var segmentsRemaining = mappedMaze.sumOf { it.size }

    init {
        runTaskTimerAsynchronously(plugin, 0, 10)

        taskManager.async {
            paste()
        }
    }

    override fun run() {
        if (segmentsRemaining == 0) {
            println("Took ${System.currentTimeMillis() - startTime}ms to generate maze for world=${world.name}")
            cancel()
            return
        }

        println("${percentagePasted()}% generated maze for world=${world.name}")
    }

    private fun paste() {
        var currentVector = startingVector

        mappedMaze.forEachIndexed { i, segments ->
            segments.forEachIndexed { _, segment ->
                segmentsRemaining--

                template.pasteSegment(segment.segmentType, segment.rotation, world, currentVector)

                if (segmentsRemaining == 0) {
                    template.pasteExitSegment(world, currentVector)
                } else {
                    template.pasteConnectorSegments(segment, world, currentVector)
                }

                currentVector = currentVector.add(0, 0, template.sizeWithDepth)
            }

            currentVector = startingVector.add(template.sizeWithDepth * (i + 1), 0, 0)
        }

        taskManager.sync {
            onFinish()
        }
    }

    private fun percentagePasted() = "%.2f".format(100 - ((segmentsRemaining.toDouble() / mappedMaze.sumOf { it.size }) * 100))
}