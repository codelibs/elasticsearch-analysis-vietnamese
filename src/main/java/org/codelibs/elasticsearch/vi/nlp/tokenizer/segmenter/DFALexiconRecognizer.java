/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.IConstants;
import org.codelibs.elasticsearch.vi.nlp.fsm.fsa.DFA;
import org.codelibs.elasticsearch.vi.nlp.fsm.fsa.DFASimulator;
import org.codelibs.elasticsearch.vi.nlp.fsm.io.FSMUnmarshaller;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * org.codelibs.elasticsearch.vi.nlp.segmenter
 * <p>
 * Nov 12, 2007, 8:44:14 PM
 * <p>
 * A recognizer for Vietnamese lexicon that uses an internal DFA representation.
 */
public final class DFALexiconRecognizer extends AbstractLexiconRecognizer {

    private static final Logger logger = LogManager.getLogger(DFALexiconRecognizer.class);

    private static DFA lexiconDFA = null;

    private static DFASimulator simulator = null;

    private static DFALexiconRecognizer recognizer = null;

    /**
     * Private constructor.
     * @param dfaLexiconFilename
     */
    private DFALexiconRecognizer(final String dfaLexiconFilename) {
        if (lexiconDFA == null) {
            // build the lexicon DFA
            logger.info("Load the lexicon automaton... ");
            lexiconDFA = (DFA) new FSMUnmarshaller().unmarshal(dfaLexiconFilename, IConstants.FSM_DFA);
            logger.info("OK.");
        }
    }

    /**
     * @param dfaLexiconFilename the DFA lexicon filen
     * @return The singleton instance of the lexicon DFA.
     */
    public static DFALexiconRecognizer getInstance(final String dfaLexiconFilename) {
        if (recognizer == null) {
            recognizer = new DFALexiconRecognizer(dfaLexiconFilename);
        }
        return recognizer;
    }

    /**
     * @return the DFA simulator
     */
    private DFASimulator getDFASimulator() {
        if (simulator == null) {
            simulator = (DFASimulator) lexiconDFA.getSimulator();
        }
        return simulator;
    }

    /* (non-Javadoc)
     * @see vn.hus.segmenter.AbstractLexiconRecognizer#accept(java.lang.String)
     */
    @Override
    public boolean accept(final String token) {
        return getDFASimulator().accept(token);
    }

    /* (non-Javadoc)
     * @see vn.hus.segmenter.AbstractLexiconRecognizer#dispose()
     */
    @Override
    public void dispose() {
        lexiconDFA.dispose();
    }
}
