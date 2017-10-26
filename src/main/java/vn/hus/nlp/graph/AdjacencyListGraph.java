/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.graph;

import vn.hus.nlp.graph.util.AdjacencyListVertexIterator;
import vn.hus.nlp.graph.util.VertexIterator;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * Oct 18, 2007, 10:41:04 PM
 * <p>
 * The adjacency list representation is suitable for sparse graph.
 */
public class AdjacencyListGraph extends Graph {

	private final Node adj[];

	/**
	 * Constructor.
	 * @param n number of vertices of the graph.
	 * @param directed <code>true/false</code>
	 */
	public AdjacencyListGraph(final int n, final boolean directed) {
		super(n, directed);
		adj = new Node[n];
	}

	/* (non-Javadoc)
	 * @see vn.hus.graph.Graph#edge(int, int)
	 */
	@Override
	public boolean edge(final int u, final int v) {
		final VertexIterator iterator = vertexIterator(u);
		while (iterator.hasNext()) {
			final int w = iterator.next();
			if (v == w) {
                return true;
            }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see vn.hus.graph.Graph#iterator(int)
	 */
	@Override
	public VertexIterator vertexIterator(final int u) {
		return new AdjacencyListVertexIterator(this, u);
	}

	/* (non-Javadoc)
	 * @see vn.hus.graph.Graph#insert(vn.hus.graph.Edge)
	 */
	@Override
	public void insert(final Edge edge) {
		final int u = edge.getU();
		final int v = edge.getV();
		// add the edge (u,v)
		adj[u] = new Node(v, adj[u]);
		// add the edge (v,u) if the graph
		// is not directed
		if (!directed) {
			adj[v] = new Node(u, adj[v]);
		}
		// increase the number of edges
		cE++;
	}

	/* (non-Javadoc)
	 * @see vn.hus.graph.IGraph#remove(vn.hus.graph.Edge)
	 */
	@Override
	public void remove(final Edge edge) {
		//TODO
	}

	/**
	 * Get the adjacency list.
	 * @return the adjacency list.
	 */
	public Node[] getAdj() {
		return adj;
	}

	/* (non-Javadoc)
	 * @see vn.hus.graph.Graph#dispose()
	 */
	@Override
	protected void dispose() {
		// delete the array of linked-list.
		for (final Node element : adj) {
			dispose(element);
		}
	}

	/**
	 * Dispose a LIFO linked list headed by a node.
	 * @param node the top node of the list.
	 */
	private void dispose(Node node) {
		if (node != null) {
			dispose(node.getNext());
		}
		node = null;
	}


}
