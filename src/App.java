import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.JOptionPane;

/**
 * This class implements the solution to the Phoenician problem, using resources implemented in the other classes in this folder,
 * such as a graph datastructure and the breadth-first algorithm.
 * @author Ricardo Süffert
 */
public class App {
    public static void main(String[] args) {
        // get the path to the file that contains the map
        String mapPath = JOptionPane.showInputDialog(null, "Digite o caminho para o arquivo do mapa:", 
                                               "Qual o arquivo do mapa?", JOptionPane.INFORMATION_MESSAGE);
        
        // create a graph that contains the information about the map
        BagGraph mapGraph = null;
        try {
            mapGraph = new BagGraph(Paths.get(mapPath));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Não foi possível ler o arquivo de entrada!\nO programa será abortado", 
                                          "Erro de I/O!", JOptionPane.ERROR_MESSAGE);
        }

        // find out the distances from the first to the last port and from the last to the first port
        int firstToLastDistance = travelToLastPort(mapGraph);
        int lastToFirstDistance = travelToFirstPort(mapGraph);

        // the total fuel necessary will be equal to firstToLastDistance + lastToFirstDistance, since each movement consumes 1 unit of fuel
        int totalFuel = firstToLastDistance + lastToFirstDistance;

        JOptionPane.showMessageDialog(null, 
                                      String.format("Para viajar do porto 0 ao %c, serão necessários %dL de combustível.", 
                                                                                    mapGraph.getPortsCount()-1, totalFuel), 
                                      "RESULTADO", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tells the distance from the first port in the map to the last port in the map
     * @param mapGraph the graph
     * @return the distance from the first port to the last port in the graph
     */
    public static int travelToLastPort(BagGraph mapGraph) {
        int distance = 0;

        for (int i=0; i<mapGraph.getPortsCount(); i++) { // for each port in the map
            int originPortCode = mapGraph.translatePortToCode(i);
            int destinationPortCode = mapGraph.translatePortToCode(i+1);

            BreadthFirstSearch bfs = new BreadthFirstSearch(mapGraph, originPortCode);
            if (bfs.hasPathTo(destinationPortCode)) { // if there is a path from this port to the next
                distance += bfs.distanceTo(destinationPortCode); // travel to that port
            }
            else { // skip the next port

            }
        }

        return distance;
    }

    /**
     * Tells the distance from the last port in the map to the last port in the map
     * @param mapGraph the graph
     * @return the distance from the last port to the first port in the graph
     */
    public static int travelToFirstPort(BagGraph mapGraph) {
        int distance = 0;

        for (int i=mapGraph.getPortsCount(); i>=0; i--) {

        }

        return distance;
    }
}
