/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.graph.util;

import org.codelibs.elasticsearch.vi.nlp.graph.IGraph;
import org.codelibs.elasticsearch.vi.nlp.graph.search.GraphDFS;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         Oct 18, 2007, 10:21:49 PM
 *         <p>
 *         The utility that provides static methods to answer queries about the
 *         connectivity of a graph, for instance count the number of components
 *         of the graph, check for connectivity between two vertices, etc.
 */
public class GraphConnectivity {

    /**
     * Get the number of connected components of the graph. We use the DFS
     * algorithm to visit all connected components.
     *
     * @param graph
     * @return the number of components of the graph
     */
    public static int countComponents(final IGraph graph) {
        final GraphDFS graphDFS = new GraphDFS(graph);
        return graphDFS.components();
    }

    /**
     * Check the connectivity between two given vertices.
     *
     * @param graph
     * @param u
     * @param v
     * @return <code>true</code> or <code>false</code>
     */
    public static boolean isConnected(final IGraph graph, final int u, final int v) {
        // search the graph with u is the start vertex.
        final GraphDFS graphDFS = new GraphDFS(graph, u);
        // test to see if u and v is in the same connected
        // component or not.
        final int[] componentId = graphDFS.getComponentId();
        return (componentId[u] == componentId[v]);
    }

    /**
     * Get all isolated vertices of a graph.
     * @param graph
     * @return An array of isolated vertices. A vertex is called isolated if it
     *         does not have an intransition.
     */
    public static int[] getIsolatedVertices(final IGraph graph) {
        final int nV = graph.getNumberOfVertices();
        final int[] vertices = new int[nV];

        int n = 0;
        for (int u = 0; u < nV; u++) {
            // Is u isolated?
            boolean isolated = true;
            for (int v = 0; v < nV; v++) {
                if (graph.edge(v, u)) {
                    isolated = false;
                }
            }
            if (isolated) {
                vertices[n++] = u;
            }
        }
        final int[] isolatedVertices = new int[n];
        for (int i = 0; i < n; i++) {
            isolatedVertices[i] = vertices[i];
        }
        return isolatedVertices;
    }
}
