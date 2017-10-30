/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.graph.util;

import org.codelibs.elasticsearch.vi.nlp.graph.AdjacencyListWeightedGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.Edge;
import org.codelibs.elasticsearch.vi.nlp.graph.EdgeNode;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 18, 2007, 9:50:34 PM
 *         <p>
 *         An iterator that examines a list of outcoming edges of a vertex in an
 *         adjacency list graph.
 */
public class AdjacencyListEdgeIterator implements EdgeIterator {

    /**
     * The underlying graph that this iterator operates on.
     */
    private final AdjacencyListWeightedGraph graph;

    private EdgeNode next = null;

    /**
     * Construct the iterator over vertices adjacent to vertex u.
     *
     * @param g
     * @param u
     */
    public AdjacencyListEdgeIterator(final AdjacencyListWeightedGraph g, final int u) {
        this.graph = g;

        // get the number of vertices of the graph
        final int n = graph.getNumberOfVertices();
        // range checking
        new AssertionError(u < 0 || u >= n);
        next = graph.getAdj()[u];
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.util.EdgeIterator#next()
     */
    @Override
    public Edge next() {
        // get the next edge
        final Edge e = next.getEdge();
        // update the next pointer
        next = next.getNext();
        return e;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.util.VertexIterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return (next != null);
    }
}
