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
 *         <p>
 *         vn.hus.tokenizer
 *         <p>
 *         Jul 28, 2007, 11:54:02 AM
 *
 * This tool finds all syllables of the word list.
 *
 */
public final class SyllablesFinder {

    private static final Logger logger = LogManager.getLogger(SyllablesFinder.class);

    /**
     * Delimiters specified by a regular expression
     */
    static final String DELIMITERS = "\\s\\d\\.,:;\\?\\^!~\\[\\]\\(\\)\\{\\}\\$&#'\"@\\|\\+-\\/";

    /**
     * A set of syllables;
     */
    SortedSet<String> syllables;

    /**
     * Default constructor
     */
    public SyllablesFinder() {
        // init the syllable tree set with a comparator for Vietnamese
        syllables = new TreeSet<>();
    }

    public void find(final String inputFile, final String outputFile) {
        // get all words of the input file
        final String[] words = UTF8FileUtility.getLines(inputFile);
        // iterate through words and build the syllables set
        for (final String word : words) {
            final String[] syls = word.split("[" + DELIMITERS + "]+");
            for (final String syl : syls) {
                if (syl.trim().length() > 0 && !CaseConverter.containsUppercase(syl)) {
                    syllables.add(syl.trim());
                }
            }
        }
        // convert the syllables set to an array of syllables
        final String[] lines = syllables.toArray(new String[syllables.size()]);

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
        new SyllablesFinder().find(args[0], args[1]);
        logger.info("Done");
    }

}
