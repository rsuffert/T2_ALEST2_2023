package pucrs.alest2.assignment2.rsuffert;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.InvalidAlgorithmParameterException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 * The {@link App} class implements the solution to the Phoenician problem, using resources implemented in the other classes in this folder,
 * such as a graph datastructure (implemented by the {@link Graph} class) and the breadth-first algorithm (implemented by the
 * {@link BreadthFirstSearch} class).
 * @author Ricardo Süffert
 */
public class App {
    private static final String NEWLINE = System.getProperty("line.separator");
    private static int lastPortVisited = 0;
    private static String inaccessiblePorts = null;

    public static void main(String[] args) {
        // get the path to the file that contains the map
        String mapPath = JOptionPane.showInputDialog(null, 
                                                     "Digite o CAMINHO (relativo ou absoluto) para o arquivo do mapa:");
        
        long initialTime = System.currentTimeMillis();
        // create a graph that contains the information about the map
        Graph mapGraph = null;
        try { 
            mapGraph = new Graph(mapPath); 
        } catch (InvalidPathException e) {
            JOptionPane.showMessageDialog(null, String.format("%s.%sO programa será encerrado.", e.getReason(), NEWLINE), 
                                          "ERRO NA EXECUÇÃO!", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Não foi possível ler o arquivo de entrada!%sVerifique o caminho e tente novamente.%sO programa será encerrado.", NEWLINE, NEWLINE), 
                                          "ERRO DE I/O!", JOptionPane.ERROR_MESSAGE);
            System.exit(-2);
        }

        // find out the distances from the first to the last port and from the last to the first port
        int firstToLastDistance = 0;
        int lastToFirstDistance = 0;
        try {
            firstToLastDistance = travelToLastPort(mapGraph);
            lastToFirstDistance = returnToFirstPort(mapGraph);
        } catch (InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(null, String.format("%s!%sO programa será encerrado.", e.getMessage(), NEWLINE),
                                          "ERRO NA EXECUÇÃO!", JOptionPane.ERROR_MESSAGE);
            System.exit(-3);
        }
        
        // the total fuel necessary will be equal to firstToLastDistance + lastToFirstDistance, since each movement consumes 1 unit of fuel
        int totalFuel = firstToLastDistance + lastToFirstDistance;

        long finalTime = System.currentTimeMillis();

        // printing the result using the Brazilian standard for separators
        Locale locale   = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        JOptionPane.showMessageDialog(null, 
                                      String.format("Para viajar do porto 1 ao %s e retornar, serão necessárias %s un. de combustível.%s%s%s%sTempo para calcular: %s seg.", 
                                                                                    nf.format(lastPortVisited), 
                                                                                    nf.format(totalFuel),
                                                                                    NEWLINE,
                                                                                    inaccessiblePorts == null? "Todos os portos são alcançáveis." : String.format("Porto(s) não alcançável(is): %s.", inaccessiblePorts),
                                                                                    NEWLINE, NEWLINE,
                                                                                    (double)(finalTime-initialTime)/1000), 
                                      "RESULTADO DA SIMULAÇÃO", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tells the distance from the first to the last port in the map, visiting, in order, all ports that are accessible. Thist method
     * also creates a string representing all ports that could not be accessed and stores it in a global variable ({@code inaccessiblePorts}).
     * @param mapGraph the map graph
     * @throws InvalidAlgorithmParameterException if {@code mapGraph} does not have any ports, or if there is no reachable port leaving from
     *                                            the first port
     * @return the distance from the first port to the last port in the graph, visiting all accessible ports (for maps that have only
     *         one port, the distance is zero)
     */
    public static int travelToLastPort(Graph mapGraph) throws InvalidAlgorithmParameterException {
        if (mapGraph.getPortsCount() == 0) throw new InvalidAlgorithmParameterException("O mapa não possui nenhum porto");
        else if (mapGraph.getPortsCount() == 1) return 0;

        ArrayList<Integer> inaccessible = new ArrayList<>();
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
            else inaccessible.add(destinationPortIdx); // add this port as inaccessible
            destinationPortIdx++; // go on to the next port
        }

        lastPortVisited = originPortIdx;

        // build a string containing the inaccessible ports to be displayed
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<inaccessible.size(); i++) {
            if (i != inaccessible.size()-1) sb.append(inaccessible.get(i) + ", ");
            else                            sb.append(inaccessible.get(i));
        }
        if (sb.length() > 0) inaccessiblePorts = sb.toString();

        // if, after checking all ports, no movement has been done, that means there are no reachable ports
        if (distance == 0) throw new InvalidAlgorithmParameterException("Não há nenhum porto alcançável partindo do primeiro");
        return distance;
    }

    /**
     * Tells the distance from the last port in the map directly to the first port in the map, without making any stops.
     * @param mapGraph the map graph
     * @throws InvalidAlgorithmParameterException if {@code mapGraph} does not have any ports, or if there is no valid path from the last
     *                                            port to the first one
     * @return the direct distance from the last port to the first port in the graph (for maps that have only
     *         one port, the distance is zero)
     */
    public static int returnToFirstPort(Graph mapGraph) throws InvalidAlgorithmParameterException {
        if (mapGraph.getPortsCount() == 0) throw new InvalidAlgorithmParameterException("O mapa não possui nenhum porto");
        else if (mapGraph.getPortsCount() == 1) return 0;

        int firstPortIdx = 1;
        int lastPortIdx  = lastPortVisited;
        int firstPortCode = mapGraph.translatePortToCode(firstPortIdx);
        int lastPortCode  = mapGraph.translatePortToCode(lastPortIdx);

        BreadthFirstSearch bfs = new BreadthFirstSearch(mapGraph, lastPortCode);
        Integer distance = bfs.distanceTo(firstPortCode);
        if (distance == null) throw new InvalidAlgorithmParameterException("Não há um caminho do último porto ao primeiro");
        return distance;
    }
}
