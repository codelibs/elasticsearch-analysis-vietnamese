/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.graph;

import org.codelibs.elasticsearch.vi.nlp.graph.util.VertexIterator;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 20, 2007, 12:52:56 PM
 *         <p>
 *         The default implementation of the graph interface.
 */
public abstract class Graph implements IGraph {
    /**
     * The graph is directed or undirected.
     */
    protected boolean directed;
    /**
     * Number of vertices.
     */
    protected int cV;

    /**
     * Number of edges.
     */
    protected int cE;

    /**
     * Constructor.
     *
     * @param n
     *            number of vertices of the graph.
     * @param directed
     *            <code>true/false</code>
     */
    public Graph(final int n, final boolean directed) {
        this.directed = directed;
        cV = n;
        cE = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#edge(int, int)
     */
    @Override
    public abstract boolean edge(int u, int v);

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#getNumberOfEdges()
     */
    @Override
    public int getNumberOfEdges() {
        return cE;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#getNumberOfVertices()
     */
    @Override
    public int getNumberOfVertices() {
        return cV;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#insert(vn.hus.graph.Edge)
     */
    @Override
    public abstract void insert(Edge edge);

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#isDirected()
     */
    @Override
    public boolean isDirected() {
        return directed;
    }

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#iterator(int)
     */
    @Override
    public abstract VertexIterator vertexIterator(int u);

    /*
     * (non-Javadoc)
     *
     * @see vn.hus.graph.IGraph#remove(vn.hus.graph.Edge)
     */
    @Override
    public abstract void remove(Edge edge);

    /**
     * Dispose the graph.
     */
    protected abstract void dispose();
}
