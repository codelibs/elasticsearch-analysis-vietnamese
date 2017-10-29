/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.graph.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.graph.AdjacencyListGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.AdjacencyMatrixGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.Edge;
import org.codelibs.elasticsearch.vi.nlp.graph.IGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.IWeightedGraph;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 21, 2007, 8:30:03 PM
 *         <p>
 *         An utility for processing graphs. This class provides methods for
 *         common access to graphs, for example, edges extraction.
 */
public class GraphUtilities {

    private static final Logger logger = LogManager.getLogger(GraphUtilities.class);

    /**
     * Extract edges of a graph.
     *
     * @param graph
     *            a graph
     * @return an array of edges of the graph.
     */
    public static Edge[] getEdges(final IGraph graph) {
        final Edge[] edges = new Edge[graph.getNumberOfEdges()];
        int e = 0;
        for (int u = 0; u < graph.getNumberOfVertices(); u++) {
            // get all vertices adjacent to u
            final VertexIterator iterator = graph.vertexIterator(u);
            while (iterator.hasNext()) {
                final int v = iterator.next();
                // create an edge (u,v)
                // we don't count for a loop edge of type (u,u)
                if (graph.isDirected() || u < v) {
                    edges[e++] = new Edge(u, v);
                }
            }
        }
        return edges;
    }

    /**
     * Extract edges of a weighted graph.
     *
     * @param graph
     *            a weighted graph
     * @return an array of edges.
     */
    public static Edge[] getWeightedEdges(final IWeightedGraph graph) {
        final Edge[] edges = new Edge[graph.getNumberOfEdges()];
        int e = 0;
        for (int u = 0; u < graph.getNumberOfVertices(); u++) {
            // get all edges adjacent to u
            final EdgeIterator iterator = graph.edgeIterator(u);
            while (iterator.hasNext()) {
                final Edge edge = iterator.next();
                if (graph.isDirected() || u < edge.getV()) {
                    edges[e++] = edge;
                }
            }
        }
        return edges;
    }

    /**
     * Copy a graph.
     *
     * @param g
     *            a graph
     * @param dense
     *            the returned graph is a dense one or not.
     * @return a dense graph that is implemented by an adjacency matrix graph or
     * a adjacency list graph.
     * @see AdjacencyMatrixGraph
     * @see AdjacencyListGraph
     */
    public static IGraph copy(final IGraph g, final boolean dense) {
        final int n = g.getNumberOfVertices();
        // create an appropriate graph
        IGraph graph = null;
        if (dense) {
            graph = new AdjacencyMatrixGraph(n, g.isDirected());
        } else {
            graph = new AdjacencyListGraph(n, g.isDirected());
        }
        // fill its edges
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (g.edge(u, v)) {
                    graph.insert(new Edge(u, v));
                }
            }
        }
        return graph;
    }

    /**
     * Get the transitive closure of a graph.
     *
     * @param g a graph.
     * @return the transitive closure of <code>g</code>.
     */
    public static IGraph getTransitiveClosure(final IGraph g) {
        // copy the original graph
        final IGraph transitiveClosure = GraphUtilities.copy(g, true);
        final int n = g.getNumberOfVertices();
        // add dummy loop edges
        for (int u = 0; u < n; u++) {
            transitiveClosure.insert(new Edge(u, u));
        }
        // the Warhall's algorithm to compute the transitive closure
        for (int v = 0; v < n; v++) {
            for (int u = 0; u < n; u++) {
                if (transitiveClosure.edge(u, v)) {
                    for (int w = 0; w < n; w++) {
                        if (transitiveClosure.edge(v, w)) {
                            transitiveClosure.insert(new Edge(u, w));
                        }
                    }
                }
            }
        }
        return transitiveClosure;
    }

    /**
     * Checks the projectivity of a graph. A graph is projective
     * if for all edges (u,v), forall k (u < k < v or v < k < u), there
     * exists a path from u to k, that is (u,k) is an edge of the transitive
     * closure of g.
     * @param g a graph
     * @return <code>true</code> or <code>false</code>
     */
    public static boolean isProjective(final IGraph g) {
        // get the transitive closure of g
        final IGraph tcg = getTransitiveClosure(g);
        final Edge[] edges = GraphUtilities.getEdges(g);
        for (final Edge e : edges) {
            final int u = e.getU();
            final int v = e.getV();
            for (int k = Math.min(u, v); k < Math.max(u, v); k++) {
                if (!tcg.edge(u, k)) {
                    logger.error("(u,k,v) = (" + u + "," + k + "," + v + ")");
                    return false;
                }
            }
        }
        return true;
    }

}
