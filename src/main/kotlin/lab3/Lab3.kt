package lab3

import lab2.Graph

object Lab3 {
    val graph: Graph = Graph(5)
    val startEdges = intArrayOf(1)
    val endEdges = intArrayOf(4, 5)
    val pList = doubleArrayOf(0.64, 0.38, 0.50, 0.51, 0.58)

    const val T = 1782
    const val K1 = 3
    const val K2 = 3

    init {
        graph.addEdge(1, intArrayOf(2, 3))
        graph.addEdge(2, intArrayOf(4))
        graph.addEdge(3, intArrayOf(4, 5))
        graph.addEdge(4, intArrayOf(5))
        graph.addEdge(5, intArrayOf())
    }

    fun calculatePWithRedundancy(
        redundancyMethod: RedundancyMethod,
        systemP: Double,
        systemQ: Double,
        avgT: Double,
        K: Int
    ) {
        val redundancyP = getRedundancyP(redundancyMethod, systemP, K)
        val redundancyQ = 1 - redundancyP
        val redundancyAvgT = calculateAvgT(redundancyP)
        val Gq = redundancyQ / systemQ
        val Gp = redundancyP / systemP
        val GT = redundancyAvgT / avgT
        println("\nPsystem = $redundancyP")
        println("Qsystem = $redundancyQ")
        println("Tavg = $redundancyAvgT")
        println("\nGq = $Gq")
        println("Gp = $Gp")
        println("GT = $GT")
        println("=".repeat(50))
    }

    private fun getRedundancyP(redundancyMethod: RedundancyMethod, systemP: Double, K: Int): Double {
        val redundancyP: Double
        if (redundancyMethod == RedundancyMethod.COMMON_LOADED) {
            println("Загальне навантажене резервування:")
            redundancyP = 1 - Math.pow(1 - systemP, (K + 1).toDouble())
        } else if (redundancyMethod == RedundancyMethod.COMMON_NOT_LOADED) {
            println("Загальне ненавантажене резервування:")
            redundancyP = 1 - Math.pow(1 - systemP, (K + 1).toDouble()) / factorial(K + 1)
        } else {
            val newPList = DoubleArray(pList.size)
            if (redundancyMethod == RedundancyMethod.DISTRIBUTED_LOADED) {
                println("Роздільне навантажене резервування:")
                for (i in pList.indices) {
                    newPList[i] = 1 - Math.pow(1 - pList[i], (K + 1).toDouble())
                }
            } else {
                println("Роздільне ненавантажене резервування:")
                for (i in pList.indices) {
                    newPList[i] = 1 - Math.pow(1 - pList[i], (K + 1).toDouble()) / factorial(K + 1)
                }
            }
            for (i in newPList.indices) {
                if (i == newPList.size - 1) System.out.printf(
                    "P%d = %.4f\n",
                    i + 1,
                    newPList[i]
                ) else System.out.printf("P%d = %.4f; ", i + 1, newPList[i])
            }
            redundancyP = graph.calculatePSystem(startEdges, endEdges, newPList)
        }
        return redundancyP
    }

    fun calculateAvgT(systemP: Double): Double {
        return -T / Math.log(systemP)
    }

    private fun factorial(n: Int): Int {
        return if (n == 0) 1 else n * factorial(n - 1)
    }
}

fun main(args: Array<String>) {
    val systemP = Lab3.graph.calculatePSystem(Lab3.startEdges, Lab3.endEdges, Lab3.pList)
    val systemAvgT = Lab3.calculateAvgT(systemP)
    val systemQ = 1 - systemP
    println("\nPsystem = $systemP")
    println("Qsystem = $systemQ")
    println("Tavg = $systemAvgT")
    println("=".repeat(50))
    Lab3.calculatePWithRedundancy(RedundancyMethod.COMMON_NOT_LOADED, systemP, systemQ, systemAvgT, Lab3.K1)
    Lab3.calculatePWithRedundancy(RedundancyMethod.COMMON_LOADED, systemP, systemQ, systemAvgT, Lab3.K2)
    Lab3.calculatePWithRedundancy(RedundancyMethod.DISTRIBUTED_NOT_LOADED, systemP, systemQ, systemAvgT, Lab3.K1)
    Lab3.calculatePWithRedundancy(RedundancyMethod.DISTRIBUTED_LOADED, systemP, systemQ, systemAvgT, Lab3.K2)
}