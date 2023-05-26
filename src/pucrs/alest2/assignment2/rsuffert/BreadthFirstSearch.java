package pucrs.alest2.assignment2.rsuffert;

import java.util.LinkedList;
import java.util.Queue;

/**
 * The {@link BreadthFirstSearch} class implements the breadth-first search algorithm, which always returns the shortest path (i.e., the path
 * that visits as few vertices as possible) between two vertices in a {@link Graph} instance.
 * @author Ricardo Süffert
 * @author Edson Moreno
 */
public class BreadthFirstSearch {
    private boolean[] marked; // each position represents whether or not the vertex represented by the index in the array (vertices generated sequentially, starting in 0) has been visited
    private int[] edgeTo; // stores the vertex that led to the vertex represented by the index in the array (stores the path)
    private int[] distTo; // stores the distance from the reference vertex to the vertex represented by the index in the array

    /**
     * Constructs an object that carries out the breadth-first algorithm for a certain reference vertex in a given graph.
     * @param g the base graph
     * @param s the reference vertex
     */
    public BreadthFirstSearch(Graph g, int s) {
        this.marked = new boolean[g.getVertexCount()];
        this.edgeTo = new int[g.getVertexCount()];
        this.distTo = new int[g.getVertexCount()];

        bfs(g, s);
    }

    /**
     * Implements the breadth-first search algorithm.
     * @param g the base graph
     * @param s the reference vertex
     */
    private void bfs (Graph g, int s) {
        Queue<Integer> q = new LinkedList<>(); // queue of the vertices whose adjacents need to be visited

        q.add(s);
        marked[s] = true;
        edgeTo[s] = -1; // no vertex led to the reference vertex (entry point)
        distTo[s] = 0; // distance from the reference vertex to itself is 0

        while (!q.isEmpty()) { // while there are vertices whose adjacent need to be visited
            int source = q.remove(); // get the source node
            for (int adj : g.adj(source)) { // for each vertex adjacent to source
                if (!marked[adj]) { // if it has not been visited
                    marked[adj] = true;
                    edgeTo[adj] = source;
                    distTo[adj] = distTo[source] + 1;
                    q.add(adj);
                }
            }
        }
    }

    /**
     * Tells whether or not a given vertex has a path to the reference vertex of this {@code BreadthFirstSearch} instance.
     * @param v the vertex to be found out whether or not it has a path to the reference vertex of this instance
     * @return {@code true} if there is a path leading from {@code v} to the reference vertex of this instance; {@code false} if not
     */
    public boolean hasPathTo(int v) { return marked[v]; }

    /**
     * Tells what the path from a given vertex to the reference vertex of this {@code BreadthFirstSearch} instance is.
     * @param v the vertex from which it is wished to find out the path to the reference vertex of this instance
     * @return the path from {@code v} to the reference vertex of this instance, or {@code null} if there isn't a path
     */
    public Iterable<Integer> pathTo(int v) {
        if (!this.hasPathTo(v)) return null;

        Bag<Integer> path = new Bag<>();
        path.add(v);
        int source = edgeTo[v];

        while (source != -1) { // while the source is not the reference vertex
            path.add(source);
            source = edgeTo[source];
        }

        return path;
    }

    /**
     * Tells how many vertices apart a given vertex is from the reference vertex of this {@code BreadthFirstSearch} instance.
     * @param v the vertex from which it is wished to find out the distance to the reference vertex of this instance
     * @return the distance from {@code v} to the reference vertex of this class, or {@code null} if there isn't a path from {@code v}
     *         to the reference vertex
     */
    public Integer distanceTo(int v) { 
        if (!this.hasPathTo(v)) return null;

        return distTo[v];
    }
}
