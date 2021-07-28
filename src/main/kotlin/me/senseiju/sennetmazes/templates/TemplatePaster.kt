package me.senseiju.sennetmazes.templates

import com.fastasyncworldedit.core.FaweAPI
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import org.bukkit.Location
import org.bukkit.scheduler.BukkitRunnable

private val taskManager = FaweAPI.getTaskManager()

class TemplatePaster(
    private val template: Template,
    private val mappedMaze: List<List<Segment>>,
    startLocation: Location,
    private val onFinish: () -> Unit
) : BukkitRunnable() {
    private val world = BukkitAdapter.adapt(startLocation.world)
    private val startingVector = BlockVector3.at(startLocation.x, startLocation.y, startLocation.z)

    private var segmentsRemaining = mappedMaze.sumOf { it.size }

    override fun run() {

    }

    fun paste() {
        taskManager.async {
            var currentVector = startingVector

            mappedMaze.forEachIndexed { i, segments ->
                segments.forEachIndexed { _, segment ->
                    template.pasteSegment(segment.segmentType, segment.rotation, world, currentVector)
                    template.pasteConnectorSegments(segment, world, currentVector)

                    currentVector = currentVector.add(0, 0, template.sizeWithDepth)

                    segmentsRemaining--
                }

                currentVector = startingVector.add(template.sizeWithDepth * (i + 1), 0, 0)
            }

            taskManager.sync {
                onFinish()
            }
        }
    }

    private fun percentagePasted() = (mappedMaze.sumOf { it.size } / segmentsRemaining) * 100
}