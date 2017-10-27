/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.lang.model.unigram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vn.hus.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Created: Apr 8, 2008, 1:02:20 PM
 *         <p>
 *         Turn a list of unigrams into a vocabulary. A vocabulary is a list of
 *         unique words which is sorted alphabetically.
 */
public final class VocabularyBuilder {

    private final List<String> vocabulary = new ArrayList<>(100);

    /**
     * Build the vocabulary from a unigram model.
     * @param unigram a unigram model
     * @param cutOff the minimal number of times that a word must occur to be included in the vocabulary
     */
    public VocabularyBuilder(final Unigram unigram, final int cutOff) {
        // build the vocabulary
        final Map<String, Integer> frequencies = Unigram.getFrequencies();
        for (final String word : frequencies.keySet()) {
            final int freq = frequencies.get(word);
            if (freq >= cutOff) {
                vocabulary.add(word);
            }
        }
        // sort the vocabulary
        Collections.sort(vocabulary);
    }

    /**
     * Build the vocabulary from a unigram model.
     * @param unigram a unigram model.
     */
    public VocabularyBuilder(final Unigram unigram) {
        this(unigram, 1);
    }

    /**
     * Print the vocabulary to a flat text file.
     * @param filename a flat text file
     */
    public void print(final String filename) {
        // create a file writer
        UTF8FileUtility.createWriter(filename);
        // create a string buffer for storing the text
        final StringBuffer sBuffer = new StringBuffer(1024);
        final Iterator<String> iter = vocabulary.iterator();
        while (iter.hasNext()) {
            final String word = iter.next();
            sBuffer.append(word + "\n");
        }
        // write the string buffer to the file
        UTF8FileUtility.write(sBuffer.toString());
        // close the writer
        UTF8FileUtility.closeWriter();
        System.err.println("# of  words = " + vocabulary.size());

    }
}
