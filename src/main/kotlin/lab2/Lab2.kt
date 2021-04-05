package lab2

fun main(args: Array<String>) {
    val graph = Graph(5)
    graph.addEdge(1, intArrayOf(2, 3))
    graph.addEdge(2, intArrayOf(4))
    graph.addEdge(3, intArrayOf(4, 5))
    graph.addEdge(4, intArrayOf(5))
    graph.addEdge(5, intArrayOf())
    val startEdges = intArrayOf(1)
    val endEdges = intArrayOf(4, 5)
    val pList = doubleArrayOf(0.64, 0.38, 0.50, 0.51, 0.58)

    val systemP = graph.calculatePSystem(startEdges, endEdges, pList)
    println("\nPsystem = $systemP")
}