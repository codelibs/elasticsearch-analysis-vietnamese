/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.graph;

import org.codelibs.elasticsearch.vi.nlp.graph.util.AdjacencyListEdgeIterator;
import org.codelibs.elasticsearch.vi.nlp.graph.util.AdjacencyListVertexIterator;
import org.codelibs.elasticsearch.vi.nlp.graph.util.EdgeIterator;
import org.codelibs.elasticsearch.vi.nlp.graph.util.VertexIterator;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 27, 2007, 10:41:04 PM
 *         <p>
 *         The adjacency list representation is suitable for sparse weighted
 *         graph. We usually use an edge iterator instead of a vertex iterator
 *         to iterate through the graph, since an edge contains not only
 *         vertices but also weight information.
 */
public class AdjacencyListWeightedGraph extends WeightedGraph {

    private final EdgeNode adj[];

    /**
     * Constructor.
     *
     * @param n
     *            number of vertices of the graph.
     * @param directed
     *            <code>true/false</code>
     */
    public AdjacencyListWeightedGraph(final int n, final boolean directed) {
        super(n, directed);
        adj = new EdgeNode[n];
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.Graph#edge(int, int)
     */
    @Override
    public boolean edge(final int u, final int v) {
        final EdgeIterator iterator = edgeIterator(u);
        while (iterator.hasNext()) {
            final Edge e = iterator.next();
            if (v == e.getV()) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.WeightedGraph#edgeIterator(int)
     */
    @Override
    public EdgeIterator edgeIterator(final int u) {
        return new AdjacencyListEdgeIterator(this, u);
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.Graph#insert(vn.hus.graph.Edge)
     */
    @Override
    public void insert(final Edge edge) {
        final int u = edge.getU();
        final int v = edge.getV();
        // add the edge (u,v)
        adj[u] = new EdgeNode(edge, adj[u]);
        // add the edge (v,u) if the graph
        // is not directed
        if (!directed) {
            adj[v] = new EdgeNode(edge, adj[v]);
        }
        // increase the number of edges
        cE++;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#remove(vn.hus.graph.Edge)
     */
    @Override
    public void remove(final Edge edge) {
        // TODO
    }

    /**
     * Get the adjacency list.
     *
     * @return the adjacency list.
     */
    public EdgeNode[] getAdj() {
        return adj;
    }

    /**
     * @see org.codelibs.elasticsearch.vi.nlp.graph.WeightedGraph#getEdge(int, int)
     * @see #edge(int, int)
     */
    @Override
    public Edge getEdge(final int u, final int v) {
        final EdgeIterator iterator = edgeIterator(u);
        while (iterator.hasNext()) {
            final Edge e = iterator.next();
            if (v == e.getV()) {
                return e;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.Graph#vertexIterator(int)
     */
    @Override
    public VertexIterator vertexIterator(final int u) {
        // build the graph2 from graph
        final int nV = getNumberOfVertices();
        final AdjacencyListGraph graph2 = new AdjacencyListGraph(nV, isDirected());
        // copy the edges of graph to graph2
        for (int v = 0; v < nV; v++) {
            final EdgeIterator edgeIterator = edgeIterator(v);
            while (edgeIterator.hasNext()) {
                final Edge edge = edgeIterator.next();
                graph2.insert(edge);
            }
        }
        // return the vertex iterator of graph2
        return new AdjacencyListVertexIterator(graph2, u);
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.Graph#dispose()
     */
    @Override
    protected void dispose() {
        // delete the array of linked-list.
        for (final EdgeNode element : adj) {
            dispose(element);
        }
    }

    /**
     * Dispose a LIFO linked list headed by a node.
     *
     * @param node
     *            the top node of the list.
     */
    private void dispose(EdgeNode node) {
        if (node != null) {
            dispose(node.getNext());
        }
        node = null;
    }

}
