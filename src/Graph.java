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

import datastructures.Bag;

/**
 * Implementation of a graph for the Phoenician problem, based off of Robert Sedgewick's and Kevin Wayne's implementation for a graph
 * of integer vertexes.
 * @author Ricardo Süffert
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public class Graph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int VERTEX_COUNT;
    private Bag<Integer>[] adj; // adj[i] contains a list of all the vertexes that are adjacent to i
    private Map<Integer, Character> codeToChar; // maps the integer vertex to its actual character value
    private Map<Character, Integer> portToCode; // maps the port code (1-9) to its code in the datastructure
     
    /**
     * CONSTRUCTOR
     * @param path the string representing the path to the file that contains the information about the graph
     * @throws IOException if the file could not be read
     * @throws InvalidPathException if the string representing the path is null
     */
    @SuppressWarnings("all")
    public Graph(String path) throws IOException, InvalidPathException {
        if (path == null) throw new InvalidPathException("mapPath", "O caminho para o mapa não pode ser nulo");

        BufferedReader reader = Files.newBufferedReader(Paths.get(path.trim()), Charset.forName("utf8"));

        String line;

        // finding out how many vertexes we have
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
            char[] lineData = splitLine(line);
            for (char vertex : lineData) { // for each vertex in the line
                int vertexCode = sequencer++;
                codeToChar.put(vertexCode, vertex); // map the vertex to an integer value
                if (Character.isDigit(vertex)) { // if the vertex is a port (digit)
                    portToCode.put(vertex, vertexCode); // map its actual value to its code in the datastructure
                } 
            }
        }

        reader.close();

        // adding the adjacencies to the graph
        final int FIRST_IN_LAST_ROW   = VERTEX_COUNT-columns;
        final int FIRST_IN_SECOND_ROW = columns;
        for (int v=0; v<VERTEX_COUNT; v++) { // for each vertex in the bag array
            adj[v] = new Bag<>();
            // theoretically, the four vertexes adjacent to the vertex in the bag array could be found as follows
            int south = v+columns;
            int north = v-columns;
            int east  = v+1;
            int west  = v-1;

            // but not all vertexes have adjacents, so we have to figure out which adjacent vertexes the current vertex has
            // and add only those which are valid to the adjacency list (also discard '*' vertexes, which are not valid)
            int currentRow        = v / columns;
            int firstInNextRow    = currentRow * columns + columns;
            int firstInCurrentRow = firstInNextRow - columns;
            boolean validSouth = (v < FIRST_IN_LAST_ROW)    && (codeToChar.get(south) != '*');
            boolean validNorth = (v >= FIRST_IN_SECOND_ROW) && (codeToChar.get(north) != '*');
            boolean validEast  = (v < firstInNextRow-1)     && (codeToChar.get(east) != '*');
            boolean validWest  = (v > firstInCurrentRow)    && (codeToChar.get(west) != '*');

            if (validSouth)  adj[v].add(south);
            if (validNorth)  adj[v].add(north);
            if (validEast)   adj[v].add(east);
            if (validWest)   adj[v].add(west);
        }
    }

    /**
     * Splits a given string (i.e., breaks it into its {@code char} values).
     * @param line the line to be split
     * @return a {@code char} array, containing all {@code char} elements in the string.
     */
    private char[] splitLine (String line) {
        char[] lineSplit = new char[line.length()];

        for (int i=0; i<line.length(); i++) {
            lineSplit[i] = line.charAt(i);
        }

        return lineSplit;
    }

    /**
     * Translates a given vertex code to its respective character (actual value)
     * @param code the vertex code
     * @return the character associated with {@code code}
     */
    public Character translateVertexCode(int code) { return codeToChar.get(code); }

    /**
     * Translates a given port number to its respective code in the graph datastructure
     * @param portNumber the number of the port
     * @return the integer value associated with the {@code portNumber} parametes, or {@code null} if the port is not in the graph
     */
    public Integer translatePortToCode(int portNumber) { 
        char characterPortNumber = Character.forDigit(portNumber, 10);
        return portToCode.get(characterPortNumber); 
    }

    /**
     * Tells how many ports there are in the map
     * @return the number of ports in the map
     */
    public int getPortsCount() { return portToCode.size(); }
 
    /**
     * Returns the number of vertices in this graph.
     *
     * @return the number of vertices in this graph
     */
    public int getVertexCount() { return VERTEX_COUNT; }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= VERTEX_COUNT)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (VERTEX_COUNT-1));
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
 