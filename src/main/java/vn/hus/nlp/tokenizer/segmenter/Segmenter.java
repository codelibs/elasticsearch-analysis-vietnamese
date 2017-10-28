/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.tokenizer.segmenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import vn.hus.nlp.graph.AdjacencyListWeightedGraph;
import vn.hus.nlp.graph.Edge;
import vn.hus.nlp.graph.IGraph;
import vn.hus.nlp.graph.IWeightedGraph;
import vn.hus.nlp.graph.Node;
import vn.hus.nlp.graph.io.GraphIO;
import vn.hus.nlp.graph.search.ShortestPathFinder;
import vn.hus.nlp.graph.util.GraphConnectivity;
import vn.hus.nlp.utils.CaseConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         vn.hus.nlp.segmenter
 *         <p>
 *         Nov 12, 2007, 8:11:26 PM
 *         <p>
 *         Segmenter of Vietnamese. It splits a chain of Vietnamese syllables
 *         (so called a phrase) into words. Before performing the segmentation,
 *         it does some necessary preprocessing:
 *         <ul>
 *         <li>If the first character of the phrase is an uppercase, it is
 *         changed to lower case.</li>
 *         <li>Normalize the phrase so that the accents of syllables are in
 *         their right places, for example, the syllable <tt>hòa</tt> is
 *         converted to <tt>hoà</tt> </li>.
 *         </ul>
 */
public class Segmenter {

    private static StringNormalizer normalizer;

    private static final Logger logger = LogManager.getLogger(Segmenter.class);

    /**
     * The DFA representing Vietnamese lexicon (the internal lexicon).
     */
    private static AbstractLexiconRecognizer lexiconRecognizer;

    /**
     * The external lexicon recognizer.
     */
    private static AbstractLexiconRecognizer externalLexiconRecognizer;

    /**
     * Result of the segmentation. A segmentation can have several results.
     * Each result is represented by an array of words.
     */
    private final List<String[]> result;

    /**
     * An ambiguity resolver.
     */
    private AbstractResolver resolver = null;

    private static double MAX_EDGE_WEIGHT = 100;

    private static boolean DEBUG = false;

    /**
     * Default constructor.
     */
    public Segmenter() {
        result = new ArrayList<>();
        // create DFA lexicon recognizer
        getDFALexiconRecognizer();
        // create external lexicon recognizer
        getExternalLexiconRecognizer();
        // create a string normalizer
        normalizer = StringNormalizer.getInstance();
    }

    /**
     * Build a segmenter with an ambiguity resolver.
     * @param resolver
     */
    public Segmenter(final AbstractResolver resolver) {
        this();
        this.resolver = resolver;
    }

    /**
     * Build a segmenter with a properties object and an ambiguity resolver.
     * @param properties
     * @param resolver
     */
    public Segmenter(final Properties properties, final AbstractResolver resolver) {
        result = new ArrayList<>();
        // create DFA lexicon recognizer
        getDFALexiconRecognizer(properties);
        // create external lexicon recognizer
        getExternalLexiconRecognizer(properties);
        // create a string normalizer
        normalizer = StringNormalizer.getInstance(properties);
        this.resolver = resolver;
    }

    /**
     * @return The result list. Each element of the list is a possible segmentation.
     * The list is normally contains less than 4 results.
     */
    public List<String[]> getResult() {
        return result;
    }

    /**
     * A pre-processing of segmentation. If the first character of the phrase is
     * an uppercase, then it is converted to the corresponding lowercase; all
     * syllables are assured to have correct accents. This method is called before
     * method {@link #segment(String)}
     *
     * @param phrase
     *            a phrase to segment
     * @return a phrase after pre-process
     */
    private static String normalize(final String phrase) {
        // 1. change the case of the first character.
        //
        final StringBuffer s = new StringBuffer(phrase);
        final char firstChar = s.charAt(0);
        char lowerChar = firstChar;
        // convert first character
        if ('A' <= firstChar && firstChar <= 'Z') {
            lowerChar = Character.toLowerCase(firstChar);
        } else if (CaseConverter.isValidUpper(firstChar)) {
            lowerChar = CaseConverter.toLower(firstChar);
        }
        s.setCharAt(0, lowerChar);
        // 2. normalize the accents of the phrase
        return normalizer.normalize(s.toString());
    }

    /**
     * @param syllables an array of syllables (a phrase)
     * @return a weighted digraph representing the phrase to be segmented. The maximum weight
     * of edges is 1.
     */
    private IWeightedGraph makeGraph(final String[] syllables) {
        final int nV = syllables.length + 1;
        final IWeightedGraph graph = new AdjacencyListWeightedGraph(nV, true);
        for (int i = 0; i < nV - 1; i++) {
            String word = "";
            int j = 0;
            while (j < nV - 1 - i) {
                // take the word syllables[i]..syllables[i+j]
                if (word.length() == 0) {
                    word = syllables[i];
                } else {
                    word = word + vn.hus.nlp.fsm.IConstants.BLANK_CHARACTER + syllables[i + j];
                }
                // check to see if the word is accepted or not
                // and create corresponding edges
                if (getDFALexiconRecognizer().accept(word) || getExternalLexiconRecognizer().accept(word)) {
                    // calculate the weight of the edge (i,i+j+1)
                    double weight = (double) 1 / (j + 1);
                    // keep only two decimal digits of weight
                    weight = Math.floor(weight * 100);
                    // insert an edge with an appropriate weight
                    graph.insert(new Edge(i, i + j + 1, weight));
                }
                j++;

            }
        }
        return graph;
    }

    /**
     * Creates an internal lexicon recognizer.
     * @return the DFA lexicon recognizer in use
     */
    private AbstractLexiconRecognizer getDFALexiconRecognizer() {
        if (lexiconRecognizer == null) {
            // use the DFA lexicon recognizer
            // user can use any lexicon recognizer here.
            lexiconRecognizer = DFALexiconRecognizer.getInstance(IConstants.LEXICON_DFA);
        }
        return lexiconRecognizer;
    }

    /**
     * Creates an internal lexicon recognizer.
     * @return the DFA lexicon recognizer in use
     */
    private AbstractLexiconRecognizer getDFALexiconRecognizer(final Properties properties) {
        if (lexiconRecognizer == null) {
            // use the DFA lexicon recognizer
            // user can use any lexicon recognizer here.
            lexiconRecognizer = DFALexiconRecognizer.getInstance(properties.getProperty("lexiconDFA"));
        }
        return lexiconRecognizer;
    }

    /**
     * Creates an external lexicon recognizer.
     * @return the external lexicon recognizer
     */
    private AbstractLexiconRecognizer getExternalLexiconRecognizer() {
        if (externalLexiconRecognizer == null) {
            externalLexiconRecognizer = new ExternalLexiconRecognizer();
        }
        return externalLexiconRecognizer;
    }

    /**
     * Creates an external lexicon recognizer.
     * @param properties
     * @return the external lexicon recognizer
     */
    private AbstractLexiconRecognizer getExternalLexiconRecognizer(final Properties properties) {
        if (externalLexiconRecognizer == null) {
            externalLexiconRecognizer = new ExternalLexiconRecognizer(properties);
        }
        return externalLexiconRecognizer;
    }

    /**
     * Try to connect an unconnected graph. If a graph is unconnected, we
     * find all of its isolated vertices and add a "fake" transition to them.
     * A vertex is called isolated if it has not any intransition.
     * @param graph a graph
     */
    private void connect(final IGraph graph) {
        // no need to connect the graph if it's connected.
        if (GraphConnectivity.countComponents(graph) == 1) {
            return;
        }

        // get all isolated vertices - vertices that do not have any intransitions.
        final int[] isolatedVertices = GraphConnectivity.getIsolatedVertices(graph);
        // info for debug
        if (DEBUG) {
            System.err.println("The graph for the phrase is: ");
            GraphIO.print(graph);
            logger.info("Isolated vertices: ");
            for (final int i : isolatedVertices) {
                logger.info(i);
            }
        }

        // There is a trick here: vertex 0 is always isolated in our linear graph since
        // it is the initial vertex and does not have any intransition.
        // We need to check whether it has an outtransition or not (its degree is not zero),
        // if no, we connect it to the nearest vertex - vertex 1 - to get an edge with weight 1.0;
        // if yes, we do nothing. Note that since the graph represents an array of non-null syllables,
        // so the number of vertices of the graph is at least 2 and it does contain vertex 1.
        boolean zeroVertex = false;
        for (final int u : isolatedVertices) {
            if (u == 0) {
                zeroVertex = true;
                /*
                GraphDegree graphDegree = new GraphDegree(graph);
                if (graphDegree.degree(0) == 0) {
                	graph.insert(new Edge(0,1,MAX_EDGE_WEIGHT));
                }
                */
                // we always add a new edge (0,1) regardless of vertex 0 is
                // of degree 0 or higher.
                graph.insert(new Edge(0, 1, MAX_EDGE_WEIGHT));
            } else {
                if (u != 1) {
                    // u is an internal isolated vertex, u > 0. We simply add an edge (u-1,u)
                    // also with the maximum weight 1.0
                    graph.insert(new Edge(u - 1, u, MAX_EDGE_WEIGHT));
                } else { // u == 1
                    if (!zeroVertex) { // insert edge (0,1) only when there does not this edge
                        graph.insert(new Edge(u - 1, u, MAX_EDGE_WEIGHT));
                    }
                }
            }
        }
        // make sure that the graph is now connected:
        if (GraphConnectivity.countComponents(graph) != 1) {
            logger.info("Hmm, fail to connect the graph!");
        }
    }

    /**
     * Prepare to segment a phrase.
     * @param phrase a phrase to be segmented.
     * @see #segment(String)
     * @return an array of syllables of the phrase
     */
    private String[] prepare(String phrase) {
        // clear the last result
        result.clear();
        // normalize the phrase
        phrase = Segmenter.normalize(phrase);
        // get syllables of the phrase
        final String[] syllables = phrase.split("\\s+");
        return syllables;
    }

    /**
     * Build a segmentation of a phrase given a path from vertex 0 to
     * the end vertex. The path must begin with vertex 0.
     * @param syllables an array of syllables
     * @param path a path, that is an array of vertices
     * @return a segmentation.
     * @see #segment(String)
     */
    private String[] buildSegmentation(final String[] syllables, final int[] path) {
        final String[] segmentation = new String[path.length - 1];
        int vertex = 0;
        int ii = 0;
        for (int k = 1; k < path.length; k++) {
            final int nextVertex = path[k];
            String word = "";
            for (int j = vertex; j < nextVertex; j++) {
                word += (syllables[j] + vn.hus.nlp.fsm.IConstants.BLANK_CHARACTER);
            }
            word = word.trim();
            segmentation[ii++] = word;
            vertex = nextVertex;
        }
        return segmentation;
    }

    /**
     * Segment a phrase.
     * @see #normalize(String)
     * @param phrase
     * @return a list of possible segmentations.
     */
    public List<String[]> segment(final String phrase) {
        // save the original phrase before normalizing it
        // objective is not to change the original words of the phrase in the
        // result segmentations.
        final String[] original = phrase.split("\\p{Space}+");
        // get syllables of the phrase
        final String[] syllables = prepare(phrase);
        // create a weighted linear graph of the phrase
        final IWeightedGraph graph = makeGraph(syllables);
        // get the end vertex of the linear graph
        final int nV = graph.getNumberOfVertices();
        // test the connectivity between the start vertex and the end vertex of
        // the graph.
        // try to connect it if it is not connected and log the abnormal phrase out
        if (!GraphConnectivity.isConnected(graph, 0, nV - 1)) {
            //			logger.log(Level.INFO, phrase);
            //			logger.log(Level.INFO, "The graph of this phrase is not connected. Try to connect it.");
            connect(graph);
        }
        // get all shortest paths from vertex 0 to the end vertex
        final ShortestPathFinder pathFinder = new ShortestPathFinder(graph);
        final Node[] allShortestPaths = pathFinder.getAllShortestPaths(nV - 1);
        // build segmentations corresponding to the shortest paths
        for (final Node path : allShortestPaths) {
            final int[] a = path.toArray();
            // get the result on the original
            final String[] segmentation = buildSegmentation(original, a);
            result.add(segmentation);
        }
        return result;
    }

    /**
     * @param segmentations a list of possible segmentations.
     * @return the most probable segmentation
     */
    public String[] resolveAmbiguity(final List<String[]> segmentations) {
        return resolver.resolve(segmentations);
    }

    /**
     * Dispose the segmenter to save space.
     */
    public void dispose() {
        result.clear();
        lexiconRecognizer.dispose();
        externalLexiconRecognizer.dispose();
    }

}
