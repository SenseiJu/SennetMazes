package me.senseiju.sennetmazes.generators

import me.senseiju.sennetmazes.generators.maze.MazeGenerator
import org.junit.jupiter.api.Test

class MazeGeneratorTest {

    @Test
    fun generateIterativeMaze() {
        val x = 10
        val y = 10
        val gen = MazeGenerator(x, y)

        displayMaze(x, y, gen.maze)
    }

    private fun displayMaze(x: Int, y: Int, maze: Array<IntArray>) {
        for (i in 0 until y) {
            // draw the north edge
            for (j in 0 until x)
                print(if (maze[j][i] and 1 == 0) "+---" else "+   ")
            println('+')

            // draw the west edge
            for (j in 0 until x)
                print(if (maze[j][i] and 8 == 0) "|   " else "    ")
            println('|')
        }

        // draw the bottom line
        for (j in 0 until x) print("+---")
        println('+')
    }
}