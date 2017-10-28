/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 * vn.hus.tokenizer
 * 2007
 */
package vn.hus.nlp.lang.model.unigram;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import vn.hus.nlp.lang.model.IConstants;
import vn.hus.nlp.lexicon.LexiconMarshaller;
import vn.hus.nlp.utils.UTF8FileUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE Hong Phuong
 *         <p>
 *         8 mars 07
 *         </p>
 *         A counter for tokens in corpus, used to produce frequencies of
 *         Vietnamese tokens.
 */
public class Unigram {

    private static final Logger logger = LogManager.getLogger(Unigram.class);

    /**
     * A map that stores strings and their corresponding frequencies.
     */
    private static Map<String, Integer> UNIGRAM;

    /**
     * The unigram model
     */
    private static Unigram MODEL;

    /**
     * Private constructor
     */
    private Unigram() {
        init();
    }

    /**
     * Initialize the map of unigrams
     */
    private void init() {
        UNIGRAM = new HashMap<>();
    }

    /**
     * Get the unique instance of a unigram model.
     * @return an empty unigram model
     */
    public static Unigram getInstance() {
        if (MODEL == null) {
            MODEL = new Unigram();
        }
        return MODEL;
    }

    /**
     * Test if a file is a directory
     * @param filename a filename
     * @return true or false
     */
    private static boolean isDirectory(final String filename) {
        final File file = new File(filename);
        return file.isDirectory();
    }

    /**
     * Load all flat text files from a directory.
     * @param directoryName name of a directory that contains corpora.
     */
    public static void loadCorpora(final String directoryName) {
        // get the corpora directory
        final File corporaDir = new File(IConstants.CORPORA_DIRECTORY);
        // list its files
        final File[] corpora = corporaDir.listFiles();
        // load all of the files
        // don't take into account subdirectories
        for (final File element : corpora) {
            final String corpus = element.getPath();
            if (!isDirectory(corpus)) {
                try {
                    loadCorpus(corpus);
                } catch (final IOException e) {
                    logger.warn(e);
                }
            }
        }
        logger.error("Total " + corpora.length + " files loaded.");
    }

    private static void processLoadedCorpus(final List<String> lines) {
        for (final String token : lines) {
            if (!UNIGRAM.containsKey(token)) {
                UNIGRAM.put(token, new Integer(1));
            } else {
                final int v = UNIGRAM.get(token).intValue();
                UNIGRAM.put(token, new Integer(v + 1));
            }
        }
    }

    public static void loadCorpusFromStream(final InputStream stream) throws IOException {
        IOUtils.readLines(stream, "UTF-8");
    }

    /**
     * Load a corpus and update the frequencies.
     *
     * @param corpus
     *            a corpus
     * @throws IOException
     */
    public static void loadCorpus(final String corpus) throws IOException {
        final List<String> lines = FileUtils.readLines(new File(corpus), "UTF-8");

        processLoadedCorpus(lines);
    }

    /**
     * Get the frequencies map.
     *
     * @return the frequencies map.
     */
    public static Map<String, Integer> getFrequencies() {
        return UNIGRAM;
    }

    /**
     * Output the unigram to a plain text file in the form of two columns.
     *
     * @param filename a flat text filename
     * @see {@link #marshal(String)}
     */
    public static void print(final String filename) {
        // create a file writer
        UTF8FileUtility.createWriter(filename);
        final Iterator<String> keys = UNIGRAM.keySet().iterator();
        // create a string buffer for storing the text
        final StringBuffer sBuffer = new StringBuffer(1024);
        int numTokens = 0;
        int freq = 0;
        while (keys.hasNext()) {
            final String token = keys.next();
            freq = UNIGRAM.get(token).intValue();
            numTokens += freq;
            sBuffer.append(token + '\t' + freq + "\n");
        }
        // write the string buffer to the file
        UTF8FileUtility.write(sBuffer.toString());
        // close the writer
        UTF8FileUtility.closeWriter();
        logger.error("# of   tokens = " + numTokens);
        logger.error("# of unigrams = " + UNIGRAM.size());
    }

    /**
     * Marshal the map to an XML file using the lexicon format.
     * @param filename the XML file containing the unigram model.
     */
    public static void marshal(final String filename) {
        new LexiconMarshaller().marshal(UNIGRAM, filename);
    }

}
