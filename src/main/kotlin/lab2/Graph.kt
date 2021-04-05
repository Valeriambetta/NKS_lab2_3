package lab2

import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.math.pow

open class Graph(edgeCount: Int) {
    private val edgeCount: Int
    private var adjacencyList: ArrayList<ArrayList<Int>> = ArrayList()
    private val allPathList: MutableList<List<Int>>

    init {
        require(edgeCount > 0) { "Edges count must be >= 1." }
        this.edgeCount = edgeCount + 1
        allPathList = ArrayList()
        init()
    }

    fun addEdge(edge: Int, adjacentEdges: IntArray) {
        for (i in adjacentEdges) {
            adjacencyList[edge].add(i)
        }
    }

    fun calculatePSystem(startEdges: IntArray, endEdges: IntArray, pList: DoubleArray): Double {
        calculateAllPaths(startEdges, endEdges)
        val listDoubleMap = calculatePStatesTable(pList)
        return listDoubleMap.values.stream().mapToDouble { obj: Double -> obj }.sum()
    }

    private fun init() {
        for (i in 0 until edgeCount) {
            adjacencyList.add(ArrayList())
        }
    }

    private fun calculateAllPaths(startEdges: IntArray, destinationEdges: IntArray) {
        allPathList.clear()
        for (startEdge in startEdges) {
            for (destinationEdge in destinationEdges) {
                val isVisited = BooleanArray(edgeCount)
                val pathList = ArrayList<Int>()
                pathList.add(startEdge)
                calculateAllPathsUtil(startEdge, destinationEdge, isVisited, pathList)
            }
        }
        allPathList.sortWith(Comparator.comparingInt<List<Int?>> { obj: List<Int?> -> obj.size })
        println("Усі можливі шляхи:")
        printPathList()
    }

    private fun calculateAllPathsUtil(
        edge: Int,
        destination: Int,
        isVisited: BooleanArray,
        localPathList: MutableList<Int>
    ) {
        if (edge == destination) {
            allPathList.add(ArrayList(localPathList))
            return
        }
        isVisited[edge] = true
        for (i in adjacencyList!![edge]) {
            if (!isVisited[i]) {
                localPathList.add(i)
                calculateAllPathsUtil(i, destination, isVisited, localPathList)
                localPathList.remove(i)
            }
        }
        isVisited[edge] = false
    }

    private fun calculatePStatesTable(pList: DoubleArray): Map<List<Int>, Double> {
        require(pList.size == edgeCount - 1) { "PList length is not equal edges count." }
        val statesPMap: MutableMap<List<Int>, Double> = HashMap()
        val states = finalStatesTable()
        for (state in states) {
            var p = if (state[0] == 1) pList[0] else 1 - pList[0]
            for (i in 1 until state.size) {
                p *= if (state[i] == 1) pList[i] else 1 - pList[i]
            }
            statesPMap[state] = p
        }
        printStatesTable(statesPMap)
        return statesPMap
    }

    private fun printPathList() {
        for (path in allPathList) {
            for (i in path.indices) {
                if (i != path.size - 1) print("E${path[i]}->") else print("E" + path[i])
            }
            println()
        }
    }

    private fun initialStatesTable(): List<List<Int>> {
        val initialTable: MutableList<List<Int>> = ArrayList()
        var temp: List<Int>
        var i = 0
        while (i < 2.0.pow((edgeCount - 1).toDouble())) {
            val binValue = Integer.toBinaryString(i)
            temp = Arrays.stream(binValue.split("").toTypedArray())
                .filter {!it.equals("")}
                .mapToInt { s: String -> s.toInt() }
                .boxed()
                .collect(Collectors.toList())
            if (temp.size < edgeCount - 1) {
                val tempSize = temp.size
                for (j in 0 until edgeCount - 1 - tempSize) {
                    temp.add(j, 0)
                }
            }
            initialTable.add(temp)
            i++
        }
        return initialTable
    }

    private fun finalStatesTable(): List<List<Int>> {
        val finalStatesTable: MutableList<List<Int>> = ArrayList()
        val initStatesTable = initialStatesTable()
        for (allWay in allPathList) {
            for (initialTableOfState in initStatesTable) {
                var flag = 0
                for (integer in allWay) {
                    if (initialTableOfState[integer - 1] == 1) flag++
                }
                if (flag == allWay.size) {
                    finalStatesTable.add(ArrayList(initialTableOfState))
                }
            }
        }
        return finalStatesTable
    }

    private fun printStatesTable(statesMap: Map<List<Int>, Double>) {
        println("\nТаблиця працездатних станів системи:")
        for (i in 1 until edgeCount) {
            print("E${i} | ")
        }
        println("Pstate")
        for ((states, p) in statesMap) {
            for (i in states.indices) {
                if (states[i] == 1) print(" + | ") else print(" - | ")
            }
            print("${"%.10f".format(p)}\n")
        }
    }
}