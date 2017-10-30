/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.IConstants;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.FSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.MinimalFSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.util.FSMUtilities;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.fsm
 * <p>
 * Nov 9, 2007, 5:16:23 PM
 * <p>
 * Test minimal DFA builder.
 */
public class MinimalDFAClient {

    private static final Logger logger = LogManager.getLogger(MinimalDFAClient.class);

    public static String ENGLISH_LEXICON_TXT = "samples/dicts/en/en.txt";
    public static String ENGLISH_LEXICON_XML = "samples/dicts/en/en.xml";
    public static String FRENCH_LEXICON_TXT = "samples/dicts/fr/fr.txt";
    public static String FRENCH_LEXICON_XML = "samples/dicts/fr/fr.xml";
    public static String VIETNAMESE_LEXICON_TXT = "samples/dicts/vn/vn.txt";
    public static String VIETNAMESE_LEXICON_XML = "samples/dicts/vn/vn.xml";

    /**
     * Create a minimal DFA from a dictionary file and encode the result
     * as an XML file.
     * @param dictionary a dictionary file
     * @param dfa the output dictionary XML file
     */
    public static void createMinimalDFA(final String dictionary, final String dfa) {
        // create an FSM builder of type DFA.
        //
        final FSMBuilder builder = new MinimalFSMBuilder(IConstants.FSM_DFA);
        builder.create(dictionary);
        // encode the result
        builder.encode(dfa);
        // print some statistic of the DFA:
        FSMUtilities.statistic(builder.getMachine());
        // dispose the builder to save memory
        builder.dispose();
    }

    public static void createMinimalEnglishDFA() {
        logger.info("Encode English lexicon...");
        createMinimalDFA(ENGLISH_LEXICON_TXT, ENGLISH_LEXICON_XML);
    }

    public static void createMinimalFrenchDFA() {
        logger.info("Encode French lexicon...");
        createMinimalDFA(FRENCH_LEXICON_TXT, FRENCH_LEXICON_XML);
    }

    public static void createMinimalVietnameseDFA() {
        logger.info("Encode Vietnamese lexicon...");
        createMinimalDFA(VIETNAMESE_LEXICON_TXT, VIETNAMESE_LEXICON_XML);
    }

    public static void main(final String[] args) {
        //		createMinimalEnglishDFA();
        //		createMinimalFrenchDFA();
        createMinimalVietnameseDFA();
    }
}
