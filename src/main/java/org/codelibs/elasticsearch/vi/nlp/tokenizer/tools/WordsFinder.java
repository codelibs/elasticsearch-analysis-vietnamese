/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.tools;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.utils.CaseConverter;
import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.tokenizer
 * <p>
 * Jul 29, 2007, 9:56:49 AM
 * Build a lexicon for word automaton.
 */
public final class WordsFinder {

    private static final Logger logger = LogManager.getLogger(WordsFinder.class);

    SortedSet<String> wordsSet;

    /**
     * Delimiters specified by a regular expression. This does not contain
     * space character.
     */
    static final String DELIMITERS = "\\d\\.,:;\\?\\^!~\\[\\]\\(\\)\\{\\}\\$&#'\"@\\|\\+-\\/";

    /*
     *
     */
    public WordsFinder() {
        wordsSet = new TreeSet<>();
    }

    public void find(final String inputFile, final String outputFile) {
        final String[] words = UTF8FileUtility.getLines(inputFile);

        for (final String word : words) {
            final String[] ws = word.split("[" + DELIMITERS + "]+");
            for (final String w : ws) {
                if (w.trim().length() > 0 && !CaseConverter.containsUppercase(w)) {
                    wordsSet.add(w.trim());
                }
            }
        }
        // convert the syllables set to an array of syllables
        final String[] lines = wordsSet.toArray(new String[wordsSet.size()]);

        // create a writer
        UTF8FileUtility.createWriter(outputFile);
        // save the results
        UTF8FileUtility.write(lines);
        // close the writer
        UTF8FileUtility.closeWriter();

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        if (args.length < 2) {
            logger.info("Please give two arguments: <inputFile> <outputFile>");
            return;
        }
        new WordsFinder().find(args[0], args[1]);
        logger.info("Done!");
    }

}
