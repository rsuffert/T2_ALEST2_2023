import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import datastructures.Bag;

/**
 * Implementation of a graph for the Phoenician problem, based off of Robert Sedgewick's and Kevin Wayne's implementation for a graph
 * of integer vertexes.
 * @author Ricardo Süffert
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class BagGraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int vertexCount;
    private Bag<Integer>[] adj; // adj[i] contains a list of all the vertexes that are adjacent to i
    private Map<Integer, Character> codeToChar; // maps the integer vertex to its actual character value
     
    /**
     * CONSTRUCTOR
     * @param p the path to the file that contains the information about the graph
     * @throws IOException if the file could not be read
     */
    @SuppressWarnings("all")
    public BagGraph(Path p) throws IOException {
        BufferedReader reader = Files.newBufferedReader(p, Charset.forName("utf8"));

        String line;

        // finding out how many vertexes we have
        line = reader.readLine();
        String[] linesColumns = line.split(" ");
        int lines = Integer.parseInt(linesColumns[0]);
        int columns = Integer.parseInt(linesColumns[1]);
        this.vertexCount = lines * columns;

        // initializing stuff
        this.adj = new Bag[vertexCount];
        this.codeToChar = new HashMap<>(vertexCount);

        // mapping the nodes of the graph (code -> char value)
        int sequencer = 0;
        while ((line = reader.readLine()) != null) { // for each line of input file
            String[] lineData = line.split(" ");
            for (String vertex : lineData) { // for each vertex in the line
                int vertexCode = sequencer++;
                codeToChar.put(vertexCode, vertex.charAt(0)); // map the vertex to an integer value
            }
        }

        // adding the adjacencies to the graph
        for (int i=0; i<vertexCount; i++) { // for each vertex in the bag array
            // theoretically, the four vertexes adjacent to the vertex in the bag array could be found as follows
            int south = i+lines;
            int north = i-lines;
            int east  = i+1;
            int west  = i-1;

            // but not all vertexes have adjacents, so we have to figure out which adjacent vertexes the current vertex has
            // and add only those which are valid to the adjacency list
            boolean validSouth = i < (vertexCount-columns);
            boolean validNorth = i > columns;
            boolean validEast =  i % columns != 0;
            boolean validWest =  i % lines != 0;
            if (validSouth)  adj[i].add(south);
            if (validNorth)  adj[i].add(north);
            if (validEast)   adj[i].add(east);
            if (validWest)   adj[i].add(west);
        }
    }

    /**
     * Translates a given vertex code to its respective character (actual value)
     * @param code the vertex code
     * @return the character associated with {@code code}
     */
    public Character translateVertexCode(int code) { return codeToChar.get(code); }
 
    /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int getVertexCount() { return vertexCount; }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= vertexCount)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (vertexCount-1));
    }
 
    /**
     * Returns the vertices adjacent to vertex {@code v}.
     *
     * @param  v the vertex
     * @return the vertices adjacent to vertex {@code v}, as an iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> adj(int v) { // using Iterable, the list returned is not modifiable
        validateVertex(v);
        return adj[v];
    }
 
    /**
     * Returns the degree of vertex {@code v}.
     *
     * @param  v the vertex
     * @return the degree of vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int degree(int v) {
        validateVertex(v);
        return adj[v].size();
    }
 
    /**
     * Returns a string representation of this graph.
     *
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(vertexCount + " vertices" + NEWLINE);
        for (int v = 0; v < vertexCount; v++) {
            s.append(v + ": ");
            for (int w : adj[v]) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
     
    /**
     * Returns this graph as an input for GraphViz (dot format).
     *
     * @return dot graph representation
     */    
    public String toDot() {
        // Uses a set of edges to prevent duplicates
        Set<String> edges = new HashSet<>();
        StringBuilder s = new StringBuilder();
        s.append("graph {"+NEWLINE);
        s.append("rankdir = LR;"+NEWLINE);
        s.append("node [shape = circle];"+NEWLINE);
        for (int v = 0; v < vertexCount; v++) {
            for (int w : adj[v]) {
                String edge = Math.min(v,w)+"-"+Math.max(v, w);
                if(!edges.contains(edge)) {
                    s.append(v + " -- " + w + ";"+NEWLINE);
                    edges.add(edge);
                }
            }
        }
        s.append("}");
        return s.toString();
    }
}
 