/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.graph.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.graph.IGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.IWeightedGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.io.GraphIO;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 18, 2008, 11:27:41 PM
 *         <p>
 *         Test class of the {@link FloydWarshall} class.
 */
public class FloydWarshallTest {

    private static final Logger logger = LogManager.getLogger(FloydWarshallTest.class);

    /**
     * A sample GRAPH.TXT file
     */
    public static final String INPUT_FILE = "samples/weighted/GRAPH.TXT";

    /**
     * Create a test object given an input data file.
     * @param inputFilename an input data file.
     */
    public FloydWarshallTest(final String inputFilename) {
        // scan the graph from a text file
        //
        final IGraph graph = GraphIO.scanAdjacencyListWeighted(inputFilename);
        // print out the graph
        //
        GraphIO.print(graph);
        // cast to a weighted graph if it is and do the trick
        //
        if (graph instanceof IWeightedGraph) {
            final IWeightedGraph weightedGraph = (IWeightedGraph) graph;
            // create a FW object
            final FloydWarshall fw = new FloydWarshall(weightedGraph);
            // run the FW algorithm on the graph to get the cost matrix
            final double[][] cost = fw.algorithmFloydWarshall();
            final int n = weightedGraph.getNumberOfVertices();
            // print out the cost matrix
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    logger.info(cost[i][j] + "\t");
                }
                logger.info("\n");
            }
        } else {
            logger.info("You don't provide me a weighted graph!");
        }

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new FloydWarshallTest(INPUT_FILE);
    }

}
