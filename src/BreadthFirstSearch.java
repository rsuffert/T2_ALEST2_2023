import java.util.LinkedList;
import java.util.Queue;
import datastructures.Bag;

/**
 * This class implements the breadth-first search algorithm for a graph implemented with an adjacency matrix.
 * @author Ricardo Süffert
 * @author Edson Moreno
 */
public class BreadthFirstSearch {
    private boolean[] marked;
    private int[] edgeTo;
    private int[] distTo;

    /**
     * CONSTRUCTOR.
     * @param g base graph
     * @param s reference vertex
     */
    public BreadthFirstSearch(BagGraph g, int s) {
        this.marked = new boolean[g.getVertexCount()];
        this.edgeTo = new int[g.getVertexCount()];
        this.distTo = new int[g.getVertexCount()];

        bfs(g, s);
    }

    /**
     * Implements the breadth-first search algorithm
     * @param G base graph
     * @param s reference vertex
     */
    private void bfs (BagGraph g, int s) {
        Queue<Integer> q = new LinkedList<>();

        q.add(s);
        marked[s] = true;
        edgeTo[s] = -1;
        distTo[s] = 0;

        while (!q.isEmpty()) {
            int source = q.remove();
            for (int adjAtual : g.adj(source)) {
                if (!marked[adjAtual]) {
                    edgeTo[adjAtual] = source;
                    marked[adjAtual] = true;
                    distTo[adjAtual] = distTo[source] + 1;
                    q.add(adjAtual);
                }
            }
        }
    }

    /**
     * Tells whether or not a given vertex has a path to the reference vertex of this {@code BreadthFirstSearch} instance
     * @param v the vertex to be found out whether or not it has a path to the reference vertex of this instance
     * @return {@code true} if there is a path leading from {@code v} to the reference vertex of this instance; {@code false} if not
     */
    public boolean hasPathTo(int v) { return marked[v]; }

    /**
     * Tells what the path from a given vertex to the reference vertex of this {@code BreadthFirstSearch} instance is
     * @param v the vertex from which it is wished to find out the path to the reference vertex of this instance
     * @return the path from {@code v} to the reference vertex of this instancex
     */
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;

        Bag<Integer> path = new Bag<>(); // lista encadeada onde a insercao e feita na primeira posicao (na hora de navegar, o inicio eh o ultimo nodo que foi inserido, entao eh apropriada para esse caso)
        path.add(v); // adicionando o nodo v (referencia) ao caminho
        int source = edgeTo[v]; // obter de onde o nodo v veio

        while (source != -1) { // enquanto source nao for o valor de onde o nodo referencia veio
            path.add(source); // adicionar source ao caminho
            source = edgeTo[source]; // buscar de onde veio o source
        }

        return path;
    }

    /**
     * Tells how many vertexes apart a given vertex is from the reference vertex of this {@code BreadthFirstSearch} instance
     * @param v the vertex from which it is wished to find out the distance to the reference vertex of this instance
     * @return the distance from {@code v} to the reference vertex of this class
     */
    public int distanceTo(int v) { return distTo[v]; }
}
