/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.graph;

import org.codelibs.elasticsearch.vi.nlp.graph.util.EdgeIterator;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 27, 2007, 10:51:13 PM
 *         <p>
 *         Basic implementation of weighted graphs.
 */
public abstract class WeightedGraph extends Graph implements IWeightedGraph {

    /**
     * Default constructor.
     * @param n
     * @param directed
     */
    public WeightedGraph(final int n, final boolean directed) {
        super(n, directed);
    }

    /* (non-Javadoc)
     * @see vn.hus.graph.IWeightedGraph#edgeIterator(int)
     */
    @Override
    public abstract EdgeIterator edgeIterator(int u);

    /* (non-Javadoc)
     * @see vn.hus.graph.IWeightedGraph#getEdge(int, int)
     */
    @Override
    public abstract Edge getEdge(int u, int v);

}
