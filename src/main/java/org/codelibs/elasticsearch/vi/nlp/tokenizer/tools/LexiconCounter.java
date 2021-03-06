/**
 *  @author LE Hong Phuong
 *  <p>
 *	24 mars 07
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.tools;

import java.util.Formatter;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author LE Hong Phuong
 *         <p>
 *         24 mars 07
 *         <p>
 *         vn.hus.tokenizer
 *         <p>
 *         This counter gives a statistics of lexical tokens in the Vietnamese
 *         lexicon. It counts for single and compound words of the lexicon. The
 *         purpose is to produce:
 *         <ol>
 *         <li>Number and percent of single words
 *         <li>Number and percent of two-syllable words
 *         <li>Number and percent of three-syllable words
 *         <li>Number and percent of words that have more than three syllables
 *         </ol>
 *
 */
public final class LexiconCounter {

    private static final Logger logger = LogManager.getLogger(LexiconCounter.class);

    /**
     * A lexicon filename
     */
    String lexiconFile;

    public LexiconCounter(final String lexiconFile) {
        this.lexiconFile = lexiconFile;
    }

    public void count() {
        // get all lines of the lexicon
        final String[] lines = UTF8FileUtility.getLines(lexiconFile);
        // count
        final int[] counters = { 0, 0, 0, 0, 0 };
        for (final String line : lines) {
            final String[] syllables = line.split("\\s+");
            final int len = syllables.length;
            if (0 < len) {
                if (len <= 4) {
                    counters[syllables.length - 1]++;
                } else {
                    counters[counters.length - 1]++;
                }
            }
        }
        logger.info("# of lexicon = " + lines.length);
        final Formatter formatter = new Formatter(System.out);
        try {
            for (int i = 0; i < counters.length; i++) {
                formatter.format(Locale.US, "%s %d = %d, %4.2f\n", "# of length ", i + 1, counters[i],
                        (float) counters[i] / lines.length * 100);
            }

        } finally {
            formatter.close();
        }
        // verify the total number
        int m = 0;
        for (final int counter : counters) {
            m += counter;
        }
        logger.info("Total = {}", m);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new LexiconCounter("dictionaries/words_v3_set.txt").count();
    }

}
