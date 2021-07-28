package me.senseiju.sennetmazes.generators.chunk

import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.*

class VoidChunkGenerator : ChunkGenerator() {
    override fun generateChunkData(world: World, random: Random, x: Int, z: Int, biome: BiomeGrid): ChunkData {
        return createChunkData(world)
    }
}