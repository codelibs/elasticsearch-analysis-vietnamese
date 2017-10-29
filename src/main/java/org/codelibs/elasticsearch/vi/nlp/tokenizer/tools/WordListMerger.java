/**
 *
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.tools;

import java.util.Set;
import java.util.TreeSet;

import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <br>
 * Jul 15, 2009, 4:24:30 PM
 * <br>
 * This class merge two lists of words to build a set of words. This
 * utility is used for updating word list (dictionary).
 */
public class WordListMerger {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final String file1 = "data/dictionaries/words_v3.txt";
        final String file2 = "data/dictionaries/extractedWords.txt";

        final Set<String> words = new TreeSet<>();
        final String[] a1 = UTF8FileUtility.getLines(file1);
        final String[] a2 = UTF8FileUtility.getLines(file2);

        for (final String word : a1) {
            words.add(word);
        }

        for (final String word : a2) {
            words.add(word);
        }

        final String fileOut = "data/dictionaries/words_v4.txt";

        UTF8FileUtility.createWriter(fileOut);
        UTF8FileUtility.write(words.toArray(new String[words.size()]));
        UTF8FileUtility.closeWriter();
    }

}
