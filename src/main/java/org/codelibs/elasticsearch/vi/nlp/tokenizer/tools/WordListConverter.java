/**
 *
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.tools;

import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconMarshaller;
import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <br>
 * Jul 15, 2009, 4:51:29 PM
 * <br>
 * This utility is used to convert a list of words in simple format to XML (lexicon) format.
 */
public class WordListConverter {

    private static final Logger logger = LogManager.getLogger(WordListConverter.class);

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final String fileInp = "data/dictionaries/words_v4.txt";
        final String fileOut = "data/dictionaries/words_v4.xml";

        final String[] words = UTF8FileUtility.getLines(fileInp);
        final Map<String, String> wordMap = new TreeMap<>();
        for (String word : words) {
            word = word.trim();
            if (word.length() > 0) {
                wordMap.put(word, "");
            }
        }
        final LexiconMarshaller marshaller = new LexiconMarshaller();
        marshaller.marshal(wordMap, fileOut);
        logger.info("Done");
    }

}
