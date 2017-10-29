/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.FSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.MinimalFSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.util.FSMUtilities;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.W;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.IConstants;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jun 12, 2008, 10:53:50 AM
 *         <p>
 *         This utility is used to rebuild the minimal DFA that encodes the
 *         Vietnamese dictionary. User need to run this tool after they make
 *         updates on the lexicon (for example add or remove words). This assures
 *         that the minimal DFA encoding the lexicon is updated with changes.
 *         The construction of the minimal DFA may take some time, so it is recommended
 *         that this utility is not called frequently. They are often used when
 *         user made a remarkable changes to the Vietnamese lexicon.
 */
public class DFALexiconBuilder {

    private static final Logger logger = LogManager.getLogger(DFALexiconBuilder.class);

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // load the lexicon
        final LexiconUnmarshaller lexiconUnmarshaller = new LexiconUnmarshaller();
        final Corpus lexicon = lexiconUnmarshaller.unmarshal(IConstants.LEXICON);
        final List<W> ws = lexicon.getBody().getW();
        final List<String> words = new ArrayList<>();
        for (final W w : ws) {
            words.add(w.getContent());
        }
        // create an FSM builder of type DFA.
        //
        final FSMBuilder builder = new MinimalFSMBuilder(org.codelibs.elasticsearch.vi.nlp.fsm.IConstants.FSM_DFA);
        logger.info("Updating the lexicon automaton...");
        final long startTime = System.currentTimeMillis();
        builder.create(words);
        final long endTime = System.currentTimeMillis();
        logger.error("Duration = " + (endTime - startTime) + " (ms)");
        // encode the result
        builder.encode(IConstants.LEXICON_DFA);
        // print some statistic of the DFA:
        FSMUtilities.statistic(builder.getMachine());
        // dispose the builder to save memory
        builder.dispose();
        logger.info("Lexicon automaton updated.");
    }

}
