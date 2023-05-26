package pucrs.alest2.assignment2.rsuffert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@link Graph} class is the implementation of a graph for the Phoenician problem, based off of Robert Sedgewick's and Kevin Wayne's
 * implementation for a graph of integer vertices, which uses an array of {@link Bag} elements to store the vertices of the graph and the
 * adjacency list of each one of them.
 * @author Ricardo Süffert
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Graph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int VERTEX_COUNT; // number of vertices in the graph
    private Bag<Integer>[] adj; // adj[i] contains a list of all the vertices that are adjacent to i
    private Map<Integer, Character> codeToChar; // maps the integer vertex to its actual character value
    private Map<Character, Integer> portToCode; // maps the port code (1-9) to its code in the datastructure
     
    /**
     * Constructs an empty graph based on a certain input file, which must conform with the specification for input files for this program.
     * @param path the string representing the path to the file that contains the information about the graph
     * @throws IOException if the file could not be read
     * @throws InvalidPathException if the {@code String} object representing the path is {@code null}
     */
    @SuppressWarnings("all")
    public Graph(String path) throws IOException, InvalidPathException {
        if (path == null) throw new InvalidPathException(null, "O caminho para o mapa não pode ser nulo");

        BufferedReader reader = Files.newBufferedReader(Paths.get(path.trim()), Charset.forName("utf8"));

        String line;

        // finding out how many vertices we have
        line = reader.readLine();
        String[] linesColumns = line.split(" ");
        int lines = Integer.parseInt(linesColumns[0]);
        int columns = Integer.parseInt(linesColumns[1]);
        this.VERTEX_COUNT = lines * columns;

        // initializing stuff
        this.adj        = new Bag[VERTEX_COUNT];
        this.codeToChar = new HashMap<>(VERTEX_COUNT);
        this.portToCode = new HashMap<>();

        // mapping the nodes of the graph (code -> char value)
        int sequencer = 0;
        while ((line = reader.readLine()) != null) { // for each line of input file
            for (int c=0; c<line.length(); c++) { // for each character in the line (vertex)
                char vertex = line.charAt(c);
                int vertexCode = sequencer++;
                codeToChar.put(vertexCode, vertex); // map the vertex to an integer value
                if (Character.isDigit(vertex)) { // if the vertex is a port (digit)
                    portToCode.put(vertex, vertexCode); // map its actual value to its code in the datastructure
                } 
            }
        }

        reader.close();
        fillAdj(columns);
    }

    /**
     * Fills the adjacency list of each vertex.
     * @param nColumns the number of columns in the map
     */
    private void fillAdj(int nColumns) {
        final int FIRST_IN_LAST_ROW   = VERTEX_COUNT-nColumns;
        final int FIRST_IN_SECOND_ROW = nColumns;
        for (int v=0; v<VERTEX_COUNT; v++) { // for each vertex in the bag array
            adj[v] = new Bag<>();
            // theoretically, the four vertices adjacent to the vertex in the bag array could be found as follows
            int south = v+nColumns;
            int north = v-nColumns;
            int east  = v+1;
            int west  = v-1;

            // but not all vertices have adjacents, so we have to figure out which adjacent vertices the current vertex has
            // and add only those which are valid to the adjacency list (also discard '*' vertices, which are not valid)
            int currentRow        = v / nColumns;
            int firstInNextRow    = currentRow * nColumns + nColumns;
            int firstInCurrentRow = firstInNextRow - nColumns;
            boolean validSouth = (v < FIRST_IN_LAST_ROW)    && (codeToChar.get(south) != '*');
            boolean validNorth = (v >= FIRST_IN_SECOND_ROW) && (codeToChar.get(north) != '*');
            boolean validEast  = (v < firstInNextRow-1)     && (codeToChar.get(east) != '*');
            boolean validWest  = (v > firstInCurrentRow)    && (codeToChar.get(west) != '*');

            if (validSouth) adj[v].add(south);
            if (validNorth) adj[v].add(north);
            if (validEast)  adj[v].add(east);
            if (validWest)  adj[v].add(west);
        }
    }

    /**
     * Translates a given vertex code to its respective character (actual value).
     * @param code the vertex code
     * @return the character associated with {@code code}, or {@code null} if there isn't any character associated with it
     */
    public Character translateVertexCode(int code) { return codeToChar.get(code); }

    /**
     * Translates a given port number to its respective code in the graph datastructure.
     * @param portNumber the number of the port
     * @return the integer value associated with the {@code portNumber} parametes, or {@code null} if the port is not in the graph
     */
    public Integer translatePortToCode(int portNumber) { 
        char characterPortNumber = Character.forDigit(portNumber, 10);
        return portToCode.get(characterPortNumber); 
    }

    /**
     * Tells how many ports there are in the map.
     * @return the number of ports in the map
     */
    public int getPortsCount() { return portToCode.size(); }
 
    /**
     * Returns the number of vertices in this graph.
     * @return the number of vertices in this graph
     */
    public int getVertexCount() { return VERTEX_COUNT; }
 
    /**
     * Returns the vertices adjacent to vertex {@code v}.
     * @param  v the vertex
     * @return the vertices adjacent to vertex {@code v}, as an iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<Integer> adj(int v) { // using Iterable, the list returned is not modifiable
        validateVertex(v);
        return adj[v];
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= VERTEX_COUNT)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (VERTEX_COUNT-1));
    }
 
    /**
     * Returns a string representation of this graph.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *         followed by the <em>V</em> adjacency lists
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(VERTEX_COUNT + " vertices" + NEWLINE);
        for (int v = 0; v < VERTEX_COUNT; v++) {
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
     * @return dot graph representation
     */    
    public String toDot() {
        // Uses a set of edges to prevent duplicates
        Set<String> edges = new HashSet<>();
        StringBuilder s = new StringBuilder();
        s.append("graph {"+NEWLINE);
        s.append("rankdir = LR;"+NEWLINE);
        s.append("node [shape = circle];"+NEWLINE);
        for (int v = 0; v < VERTEX_COUNT; v++) {
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
 