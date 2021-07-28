package me.senseiju.sennetmazes.generators.maze

import java.util.*
import me.senseiju.sennetmazes.Cardinal

/**
 * Base generation from https://rosettacode.org/wiki/Maze_generation#Kotlin
 * but has been redone to use iteration
 */
class MazeGenerator(private val x: Int, private val y: Int) {
    val maze = Array(x) { IntArray(y) }

    init {
        generate()
    }

    private fun generate() {
        val stack = Stack<Cell>()

        val m = Array(x) { x ->
            Array(y) { y ->
                Cell(x, y, Cardinal.shuffled())
            }
        }

        stack.push(m.random().random())

        while (stack.isNotEmpty()) {
            val cell = stack.peek()

            if (cell.order.isEmpty()) {
                stack.pop()
                continue
            }

            val nextDirection = cell.order.removeAt(0)
            val nx = cell.x + nextDirection.dx
            val ny = cell.y + nextDirection.dy

            if (between(nx, x) && between(ny, y) && maze[nx][ny] == 0) {
                maze[cell.x][cell.y] = maze[cell.x][cell.y] or nextDirection.bit
                maze[nx][ny] = maze[nx][ny] or nextDirection.opposite.bit

                stack.push(m[nx][ny])
            }
        }
    }

    private fun between(v: Int, upper: Int) = v in 0 until upper

    private class Cell(val x: Int, val y: Int, val order: ArrayList<Cardinal>)
}