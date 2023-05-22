import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 * This class implements the solution to the Phoenician problem, using resources implemented in the other classes in this folder,
 * such as a graph datastructure and the breadth-first algorithm.
 * @author Ricardo Süffert
 */
public class App {
    private static final String NEWLINE = System.getProperty("line.separator");
    private static int lastPortVisited = 0;

    public static void main(String[] args) {
        // get the path to the file that contains the map
        String mapPath = JOptionPane.showInputDialog(null, 
                                                     "Digite o CAMINHO (relativo ou absoluto) para o arquivo do mapa:");
        
        // create a graph that contains the information about the map
        Graph mapGraph = null;
        try { 
            mapGraph = new Graph(mapPath); 
        } catch (InvalidPathException e) {
            JOptionPane.showMessageDialog(null, String.format("%s.%sO programa será encerrado.", e.getReason(), NEWLINE), 
                                          "ERRO!", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Não foi possível ler o arquivo de entrada!%sVerifique o caminho e tente novamente.%sO programa será encerrado.", NEWLINE, NEWLINE), 
                                          "ERRO DE I/O!", JOptionPane.ERROR_MESSAGE);
            System.exit(-2);
        }

        if (mapGraph.getPortsCount() == 0) {
            JOptionPane.showMessageDialog(null, String.format("O mapa de entrada não possui nenhum porto.%sO programa será encerrado.", NEWLINE),
                                          "NÃO HÁ PORTOS NO MAPA!", JOptionPane.ERROR_MESSAGE);
            System.exit(-3);
        }

        // find out the distances from the first to the last port and from the last to the first port
        int firstToLastDistance = mapGraph.getPortsCount() >= 2? travelToLastPort(mapGraph)  : 0;
        int lastToFirstDistance = mapGraph.getPortsCount() >= 2? returnToFirstPort(mapGraph) : 0;

        // the total fuel necessary will be equal to firstToLastDistance + lastToFirstDistance, since each movement consumes 1 unit of fuel
        int totalFuel = firstToLastDistance + lastToFirstDistance;

        // printing the result using the Brazilian standard for separators
        Locale locale   = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        JOptionPane.showMessageDialog(null, 
                                      String.format("Para viajar do porto 1 ao %s, serão necessários %s un. de combustível.", 
                                                                                    nf.format(mapGraph.getPortsCount()), 
                                                                                    nf.format(totalFuel)), 
                                      "RESULTADO", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tells the distance from the first port in the map to the last port in the map, visiting all ports that are accessible in order.
     * @param mapGraph the graph
     * @return the distance from the first port to the last port in the graph, visiting all accessible ports
     */
    public static int travelToLastPort(Graph mapGraph) {
        int distance = 0;

        int originPortIdx      = 1;
        int destinationPortIdx = 2;
        while (destinationPortIdx <= mapGraph.getPortsCount()) { // while we haven't got to the last port yet
            int originPortCode      = mapGraph.translatePortToCode(originPortIdx);
            int destinationPortCode = mapGraph.translatePortToCode(destinationPortIdx);
            BreadthFirstSearch bfs  = new BreadthFirstSearch(mapGraph, originPortCode);
            if (bfs.hasPathTo(destinationPortCode)) { // if there's a path to the destination
                // "visit it", i.e.:
                distance += bfs.distanceTo(destinationPortCode);
                originPortIdx = destinationPortIdx; // only visit the destination if there's a valid path
            }

            destinationPortIdx++;
        }

        lastPortVisited = originPortIdx;

        return distance;
    }

    /**
     * Tells the distance from the last port in the map directly to the first port in the map, without making any stops.
     * @param mapGraph the map graph
     * @return the direct distance from the last port to the first port in the graph
     */
    public static int returnToFirstPort(Graph mapGraph) {
        int firstPortIdx = 1;
        int lastPortIdx  = lastPortVisited;

        int firstPortCode = mapGraph.translatePortToCode(firstPortIdx);
        int lastPortCode  = mapGraph.translatePortToCode(lastPortIdx);

        BreadthFirstSearch bfs = new BreadthFirstSearch(mapGraph, lastPortCode);

        return bfs.distanceTo(firstPortCode);
    }
}
