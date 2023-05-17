import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class implments an undirected graph using an adjacency matrix.
 */
public class AdjMatrixGraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int vertexCount;
    private char[][] graph;

    /**
     * CONSTRUCTOR.
     * @param p the path to the input file
     * @throws IOException if the file could not be read
     */
    public AdjMatrixGraph(Path p) throws IOException {
        BufferedReader reader = Files.newBufferedReader(p, Charset.forName("utf8"));

        // finding out how many lines and columns we have in the file
        String line = reader.readLine(); // first line: [lines columns]
        String[] linesColumns = line.split(" ");
        int lines = Integer.parseInt(linesColumns[0]);
        int columns = Integer.parseInt(linesColumns[1]);
        this.vertexCount = lines * columns;

        // filling a matrix with all vertexes (graph implemented with adjacency matrix)
        char[][] map = new char[lines][columns];
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            String[] lineData = line.split(" ");
            for (int c=0; c<lineData.length; c++) {
                map[lineCount][c] = lineData[c].charAt(0);
            }
            lineCount++;
        }
        this.graph = map;
    }

    /**
     * Tells how many vertexes there are in this graph.
     * @return the number of vertexes in this graph
     */
    public int getVertexCount() { return vertexCount; }

    /**
     * Returns the vertex associated with the coordinate given by (line, column) in the adjacency matrix that implements the graph.
     * @param line the line coordinate of the vertex to be returned
     * @param column the column coordinate of the vertex to be returned
     * @return the vertex given by the coordinate (line, column) in the adjacency matrix, if the given line and column are valid; 
     *         {@code null} otherwise
     */
    public Character getVertex(int line, int column) { 
        if ((line >= 0 && line < graph.length) &&
            (column >= 0 && column <= graph[line].length)) {
            return graph[line][column];
        }

        return null;
    }

    /**
     * Builds and returns a string representation of the graph.
     * @return the string representing the graph
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int l=0; l<graph.length; l++) {
            for (int c=0; c<graph[l].length; c++) {
                sb.append(graph[l][c]);
            }
            sb.append(NEWLINE);
        }

        return sb.toString();
    }
}
