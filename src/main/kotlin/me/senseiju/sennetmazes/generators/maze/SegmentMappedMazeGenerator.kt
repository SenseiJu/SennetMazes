package me.senseiju.sennetmazes.generators.maze

import me.senseiju.sennetmazes.Cardinal
import me.senseiju.sennetmazes.generators.maze.MazeGenerator
import me.senseiju.sennetmazes.templates.Segment
import me.senseiju.sennetmazes.templates.SegmentType

private class SegmentMappedMazeGenerator(x: Int, y: Int) {
    private val maze = MazeGenerator(x, y).maze

    fun mapMazeToSegments(): List<List<Segment>> {
        val mappedMaze = ArrayList<List<Segment>>()

        maze.forEachIndexed { i, ints ->
            mappedMaze.add(ints.mapIndexed { j, _ -> createSegment(i, j) })
        }

        return mappedMaze
    }

    private fun createSegment(i: Int, j: Int): Segment {
        val openEdges = getOpenEdges(i, j)

        /**
         * You may notice the rotation's seem like they are in the wrong direction, that's because when rotating you
         * must be looking from the ground up
         */
        with (openEdges) {
            return when (openEdges.size) {
                1 -> {
                    when {
                        contains(Cardinal.N) -> Segment(SegmentType.U, 0.0, openEdges)
                        contains(Cardinal.E) -> Segment(SegmentType.U, 270.0, openEdges)
                        contains(Cardinal.S) -> Segment(SegmentType.U, 180.0, openEdges)
                        else -> Segment(SegmentType.U, 90.0, openEdges)
                    }
                }
                2 -> {
                    when {
                        containsAll(setOf(Cardinal.N, Cardinal.S)) -> Segment(SegmentType.STRAIGHT, 0.0, openEdges)
                        containsAll(setOf(Cardinal.E, Cardinal.W)) -> Segment(SegmentType.STRAIGHT, 90.0, openEdges)
                        containsAll(setOf(Cardinal.E, Cardinal.S)) -> Segment(SegmentType.CORNER, 0.0, openEdges)
                        containsAll(setOf(Cardinal.S, Cardinal.W)) -> Segment(SegmentType.CORNER, 270.0, openEdges)
                        containsAll(setOf(Cardinal.W, Cardinal.N)) -> Segment(SegmentType.CORNER, 180.0, openEdges)
                        else -> Segment(SegmentType.CORNER, 90.0, openEdges)
                    }
                }
                3 -> {
                    when {
                        containsAll(setOf(Cardinal.N, Cardinal.E, Cardinal.S)) -> Segment(SegmentType.T, 0.0, openEdges)
                        containsAll(setOf(Cardinal.E, Cardinal.S, Cardinal.W)) -> Segment(SegmentType.T, 270.0, openEdges)
                        containsAll(setOf(Cardinal.S, Cardinal.W, Cardinal.N)) -> Segment(SegmentType.T, 180.0, openEdges)
                        else -> Segment(SegmentType.T, 90.0, openEdges)
                    }
                }
                else -> Segment(SegmentType.CROSSROADS, 0.0, openEdges)
            }
        }
    }

    private fun getOpenEdges(i: Int, j: Int): Set<Cardinal> {
        val openEdges = Cardinal.values().toHashSet()

        val cell = maze[i][j]
        if (cell and 1 == 0) {
            openEdges.remove(Cardinal.N)
        }
        if (cell and 2 == 0) {
            openEdges.remove(Cardinal.S)
        }
        if (cell and 4 == 0) {
            openEdges.remove(Cardinal.E)
        }
        if (cell and 8 == 0) {
            openEdges.remove(Cardinal.W)
        }

        return openEdges
    }
}

fun generateSegmentMappedMaze(x: Int, y: Int) = SegmentMappedMazeGenerator(x, y).mapMazeToSegments()