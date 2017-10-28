/**
 *
 */
package vn.hus.nlp.tokenizer.tools;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import vn.hus.nlp.utils.CaseConverter;
import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;
import vn.hus.nlp.utils.UTF8FileUtility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <br>
 * Jul 15, 2009, 2:18:40 PM
 * <br>
 * This extractor extracts set of words from a manually tagged corpus. This utility is used to enrich
 * the dictionary of the tokenizer.
 */
public class WordExtractor {

    private static final Logger logger = LogManager.getLogger(WordExtractor.class);

    /**
     * The extension of tagged data file of the VTB treebank.
     */
    static String POS_FILE_EXTENSION = ".pos";

    static boolean PRUNE_NAME = true;

    /**
     * Gets the words of a tagged sentence. In the tagged sentence,
     * word/tag pairs are separated by space characters.
     * @param taggedSentence a tagged sentence.
     * @return a set of words.
     */
    public static Set<String> getWords(final String taggedSentence) {
        final Set<String> words = new HashSet<>();
        final String[] pairs = taggedSentence.split("\\s+");
        int slashIndex = -1;
        for (final String pair : pairs) {
            slashIndex = pair.indexOf('/');
            if (slashIndex > 0) {
                String word = pair.substring(0, slashIndex);
                // replace all the _ with spaces
                word = word.replaceAll("_", " ").trim();
                if (PRUNE_NAME) {
                    if (!containsStopwords(word) && !isName(word)) {
                        // make the word lowercase
                        words.add(CaseConverter.toLower(word));
                    }
                } else {
                    words.add(word);
                }
            }
        }
        return words;
    }

    private static boolean containsStopwords(final String word) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == '.' || word.charAt(i) == ',' || word.charAt(i) == '-') {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a word is a name or not
     * @param word
     * @return
     */
    private static boolean isName(final String word) {
        final String[] syllables = word.split("\\s+");

        if (syllables.length == 1) {
            final String s = syllables[0];
            if (s.length() > 0) {
                final char c = s.charAt(0);
                if ((c >= 'A' && c <= 'Z') || CaseConverter.isValidUpper(c)) {
                    return true;
                }
            }
        }

        for (final String s : syllables) {
            if (s.length() > 0) {
                final char c = s.charAt(0);
                if ((c >= 'a' && c <= 'z') || CaseConverter.isValidLower(c)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Extracts all words of tagged data file in a directory.
     * @param directoryName
     * @return a set of words
     */
    public static Set<String> extract(final String directoryName) {
        final Set<String> words = new TreeSet<>();
        // create a file filter
        final TextFileFilter filter = new TextFileFilter(POS_FILE_EXTENSION);

        final File directory = new File(directoryName);

        final File[] files = FileIterator.listFiles(directory, filter);
        System.err.println("# of files = " + files.length);

        for (final File file : files) {
            // get sentences
            final String[] sentences = UTF8FileUtility.getLines(file.getAbsolutePath());
            for (final String s : sentences) {
                words.addAll(getWords(s));
            }

        }

        return words;
    }

    public static void main(final String[] args) {
        final String directoryName = "data/VTB-20090712";
        final Set<String> words = WordExtractor.extract(directoryName);
        // write out the result
        final String fileOut = "data/dictionaries/extractedWords.aut.txt";
        UTF8FileUtility.createWriter(fileOut);
        UTF8FileUtility.write(words.toArray(new String[words.size()]));
        UTF8FileUtility.closeWriter();
        logger.info("Done.");
    }
}
