/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.graph.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.graph.IGraph;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 21, 2007, 11:56:01 PM
 *         <p>
 *         This class provides a way for client to find the degree of a
 *         vertex in a graph in constant time, after linear-time preprocessing
 *         in the constructor. We use a vertex-indexed array to do the trick.
 */
public final class GraphDegree {
    private static final Logger logger = LogManager.getLogger(GraphDegree.class);

    private final IGraph graph;
    private final int[] deg;

    /**
     * Constructor.
     *
     * @param g
     */
    public GraphDegree(final IGraph g) {
        this.graph = g;
        final int n = graph.getNumberOfVertices();
        deg = new int[n];
        for (int u = 0; u < n; u++) {
            deg[u] = 0;
            final VertexIterator iterator = graph.vertexIterator(u);
            while (iterator.hasNext()) {
                iterator.next();
                deg[u]++;
            }
        }
    }

    /**
     * Get the degree of a vertex.
     *
     * @param u
     * @return the degree of a vertex
     */
    public int degree(final int u) {
        return deg[u];
    }

    /**
     * Print degrees of all the vertices.
     */
    public void printDegrees() {
        final int n = graph.getNumberOfVertices();
        for (int u = 0; u < n; u++) {
            final int d = degree(u);
            // for testing only:
            logger.info("deg(" + u + ") = " + d);
        }

    }
}
