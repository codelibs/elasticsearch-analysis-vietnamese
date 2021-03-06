/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.graph.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.graph.Edge;
import org.codelibs.elasticsearch.vi.nlp.graph.IGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.util.VertexIterator;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 23, 2007, 12:07:28 AM
 *         <p>
 *         Search the graph using the depth first search algorithm.
 */
public class GraphDFS {
    private static final Logger logger = LogManager.getLogger(GraphDFS.class);

    private IGraph graph;
    private int count;
    private int[] order;
    private int[] spanningTree;
    private int[] componentId;
    private int comp;

    /**
     * Init the data.
     *
     * @param g
     */
    private void init(final IGraph g) {
        this.graph = g;
        count = 0;
        // the numeber of components of the graph
        comp = 0;
        // get the number of vertices of the graph
        final int n = graph.getNumberOfVertices();
        // init the order and the spanning tree array
        order = new int[n];
        spanningTree = new int[n];
        // init the component.
        componentId = new int[n];
        for (int v = 0; v < n; v++) {
            order[v] = -1;
            spanningTree[v] = -1;
            componentId[v] = -1;
        }
    }

    public GraphDFS(final IGraph g) {
        init(g);
    }

    /**
     * Search vertices of the graph in the same connected component as u.
     *
     * @see #search(int)
     * @param g
     * @param u
     */
    public GraphDFS(final IGraph g, final int u) {
        this(g);
        // search the graph from u
        search(u);
    }

    /**
     * Search vertices of the graph in the same connected component with an edge
     * e and build the spanning tree of the component.
     *
     * @see #search(Edge)
     * @param g
     * @param e
     */
    public GraphDFS(final IGraph g, final Edge e) {
        this(g);
        // search the graph from e
        search(e);
    }

    /**
     * Search (visit) all vertices in the same connected component as a vertex
     * <code>u</code>. If the graph is not connected, it may have more than one
     * connected component, and the vertices of components that do not contain
     * <code>u</code> may not be visited.
     *
     * @param u
     *            a vertex.
     */
    private void search(final int u) {
        // u is visited with order count
        order[u] = count++;
        // mark the component id of u
        componentId[u] = comp;

        final VertexIterator iterator = graph.vertexIterator(u);
        while (iterator.hasNext()) {
            final int v = iterator.next();
            if (order[v] == -1) { // v is not visited
                // visit v recursively
                search(v);
            }
        }
    }

    /**
     * Search (visit) all vertices in a same connected component. We pass an
     * edge to the search method. This method also builds a spanning tree of the
     * graph with a parent-link representation. We can find any given vertex's
     * parent in the tree (<tt>spanningTree</tt>) or any given vertex's order in
     * the search.
     *
     * @param edge
     */
    private void search(final Edge edge) {
        final int u = edge.getU();
        final int v = edge.getV();
        // mark the component id of u and v
        componentId[u] = comp;
        componentId[v] = comp;

        order[v] = count++;
        // u is the parent of v
        spanningTree[v] = u;
        // iterate through all children of v
        final VertexIterator iterator = graph.vertexIterator(v);
        while (iterator.hasNext()) {
            final int w = iterator.next();
            if (order[w] == -1) { // w is not visited
                // visit w recursively
                search(new Edge(v, w));
            }
        }

    }

    /**
     * Get the number of vertices encountered during the search.
     *
     * @return number of vertices encountered during the search.
     */
    public int count() {
        return count;
    }

    /**
     * Get the order in which the search visited a vertex.
     *
     * @param u
     *            a vertex
     * @return the order in which the search visited a vertex.
     */
    public int order(final int u) {
        return order[u];
    }

    /**
     * Get the parent vertex of a vertex in the spanning tree.
     *
     * @param u
     * @return the parent vertex of a vertex in the spanning tree.
     */
    public int spanningTree(final int u) {
        return spanningTree[u];
    }

    public void printOrder() {
        for (int u = 0; u < graph.getNumberOfVertices(); u++) {
            final int o = order[u];
            logger.info(u + ": " + o);
        }
    }

    /**
     * Count the number of components of a graph. A undirected graph is
     * connected if this method returns 1. The number of components is precisely
     * the number of times we call method search.
     *
     * @return the number of components of the graph.
     */
    public int components() {
        // scan the order array from left to right to find
        // a vertex that has not been visited.
        for (int u = 0; u < graph.getNumberOfVertices(); u++) {
            if (order[u] == -1) {
                comp++;
                search(u);
            }
        }
        // print the component id array
        // for (int u = 0; u < graph.getNumberOfVertices(); u++) {
        // logger.info("componentId[" + u + "] = " + componentId[u]);
        // }
        return comp;
    }

    public int[] getOrder() {
        return order;
    }

    /**
     * Get the component id array of the graph. If two vertices have the same
     * component ids, they will be in a connected component.
     *
     * @return the component id.
     */
    public int[] getComponentId() {
        return componentId;
    }
}
