/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 * vn.hus.tokenizer
 * 2007
 */
package vn.hus.nlp.lang.model.bigram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import vn.hus.nlp.lang.model.IConstants;
import vn.hus.nlp.lexicon.LexiconMarshaller;
import vn.hus.nlp.utils.UTF8FileUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE Hong Phuong
 * <p>
 * 13 mars 07
 * </p>
 * A counter for two sequential tokens in corpus (bigram model),
 * used to produce frequencies of bigrams of Vietnamese tokens.
 *
 */
public class Bigram {

    private static final Logger logger = LogManager.getLogger(Bigram.class);

    /**
     * A map of couples. We use a map to speed up the search of a couple.
     *
     */
    private Map<Couple, Couple> bigram;

    public Bigram() {
        init();
        loadCorpora();
    }

    public Bigram(final boolean isCoded) {
        init();
        // load corpora, do statistics
        loadCorpora();
    }

    /**
     * Load all corpora.
     *
     */
    private void loadCorpora() {
        // get the corpora directory
        final File corporaDir = new File(IConstants.CORPORA_DIRECTORY);
        // list its files
        final File[] corpora = corporaDir.listFiles();
        for (final File element : corpora) {
            final String corpus = element.getPath();
            if (!isDirectory(corpus)) {
                try {
                    loadCorpus(corpus);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("Total " + corpora.length + " files loaded.");
    }

    private boolean isDirectory(final String filename) {
        final File file = new File(filename);
        return file.isDirectory();
    }

    /**
     * Load a corpus and update the bigram set
     * @param corpus
     * @throws IOException
     */
    private void loadCorpus(final String corpus) throws IOException {

        final String[] lines = UTF8FileUtility.getLines(corpus);
        String first = "";
        for (final String second : lines) {
            final Couple couple = new Couple(first, second);
            if (!bigram.keySet().contains(couple)) {
                bigram.put(couple, couple);
            } else {
                // search for the couple
                final Couple c = bigram.get(couple);
                c.incFreq();
            }
            // update the first token
            first = second;
        }
    }

    private void init() {
        bigram = new HashMap<>();
    }

    /**
     * Get the bigram set.
     * @return
     */
    public Map<Couple, Couple> getBigram() {
        return bigram;
    }

    /**
     * Output bigram to a text file.
     * @param filename
     * @see {@link #marshalResults(String)}.
     */
    public void print(final String filename) {
        try {
            final Writer writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            final BufferedWriter bufWriter = new BufferedWriter(writer);
            final Iterator<Couple> couples = bigram.keySet().iterator();
            while (couples.hasNext()) {
                final Couple couple = couples.next();
                bufWriter.write(couple + "\n");
            }
            bufWriter.flush();
            writer.close();
            logger.info("# of couples = " + bigram.size());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Marshal the map to an xml file using the lexicon format.
     * @param filename
     */
    public void marshal(final String filename) {
        // prepare a map for marshalling
        final Map<String, Integer> map = new HashMap<>();
        for (final Couple c : bigram.keySet()) {
            final String key = c.getFirst() + "," + c.getSecond();
            final int value = c.getFreq();
            map.put(key, value);
        }
        new LexiconMarshaller().marshal(map, filename);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Bigram counter = new Bigram(false);
        counter.marshal(IConstants.BIGRAM_MODEL);
        logger.info("Done!");
    }

}
