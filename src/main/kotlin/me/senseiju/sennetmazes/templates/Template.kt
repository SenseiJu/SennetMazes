package me.senseiju.sennetmazes.templates

import com.fastasyncworldedit.core.FaweAPI
import com.fastasyncworldedit.core.util.EditSessionBuilder
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.math.transform.AffineTransform
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.World
import me.senseiju.sennetmazes.Cardinal
import me.senseiju.sennetmazes.SennetMazes
import me.senseiju.sennetmazes.exceptions.InvalidTemplateSegmentFormatException
import java.io.File
import java.io.FileInputStream
import java.util.*

class Template(
    plugin: SennetMazes,
    private val name: String,
    private val segmentSize: Int,
    private val connectorDepth: Int,
    private val playerSpawnOffset: BlockVector3
) {
    private val segments = EnumMap<SegmentType, Clipboard>(SegmentType::class.java)

    init {
        SegmentType.values().forEach {
            if (it == SegmentType.CONNECTOR && connectorDepth == 0) return@forEach

            segments[it] = loadSchematic(plugin, it)
        }
    }

    private fun loadSchematic(plugin: SennetMazes, segment: SegmentType): Clipboard {
        val file = File(plugin.dataFolder.absolutePath + "/templates/$name/${segment.toSchemFileName()}")
        val format = ClipboardFormats.findByFile(file) ?: throw InvalidTemplateSegmentFormatException(name, segment)

        return FileInputStream(file).use {
            format.getReader(it).read()
        }
    }

    private fun pasteSegment(segment: SegmentType, rotation: Double, world: World, vector: BlockVector3) {
        val clipboardHolder = ClipboardHolder(segments[segment] ?: return)
        clipboardHolder.transform = AffineTransform().rotateY(rotation)

        val rotationOffset = when (rotation) {
            270.0 -> BlockVector3.at(-1, 0, -segmentSize)
            180.0 -> BlockVector3.at(segmentSize - 1, 0, -(segmentSize + 1))
            90.0 -> BlockVector3.at(segmentSize, 0, -1)
            else -> BlockVector3.ZERO
        }

        EditSessionBuilder(world).build().use {
            val operation = clipboardHolder
                .createPaste(it)
                .to(vector.add(rotationOffset))
                .build()

            Operations.complete(operation)
        }
    }

    fun pasteMaze(
        mappedMaze: List<List<Segment>>,
        world: World,
        startingVector: BlockVector3,
        onFinish: () -> Unit
    ) {
        FaweAPI.getTaskManager().async {
            var currentVector = startingVector

            mappedMaze.forEachIndexed { i, segmentRotations ->
                segmentRotations.forEachIndexed { _, segmentRotation ->
                    pasteSegment(
                        segmentRotation.segmentType,
                        segmentRotation.rotation,
                        world,
                        currentVector
                    )

                    pasteConnectorSegments(segmentRotation, world, currentVector)

                    currentVector = currentVector.add(0, 0, (segmentSize + connectorDepth))
                }

                currentVector = startingVector.add((segmentSize + connectorDepth) * (i + 1), 0, 0)
            }

            onFinish()
        }
    }


    private fun pasteConnectorSegments(segment: Segment, world: World, vector: BlockVector3) {
        var currentVector = vector

        if (connectorDepth > 0) {
            currentVector = currentVector.add(segmentSize, 0, 0)

            if (segment.openEdges.contains(Cardinal.E)) {
                pasteSegment(
                    SegmentType.CONNECTOR,
                    270.0,
                    world,
                    currentVector
                )
            }

            currentVector = currentVector.add(-segmentSize, 0, connectorDepth)

            if (segment.openEdges.contains(Cardinal.S)) {
                pasteSegment(
                    SegmentType.CONNECTOR,
                    0.0,
                    world,
                    currentVector
                )
            }
        }
    }
}