/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.test;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.FSM;
import org.codelibs.elasticsearch.vi.nlp.fsm.IConstants;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.FSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.MinimalFSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.builder.SimpleFSMBuilder;
import org.codelibs.elasticsearch.vi.nlp.fsm.io.FSMUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.fsm.util.FSMUtilities;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.fsm
 * <p>
 * Nov 9, 2007, 5:16:23 PM
 * <p>
 */
public class DFAClient {

    private static final Logger logger = LogManager.getLogger(DFAClient.class);

    /**
     * Test some operations on fsm.
     */
    public static void testOperations() {
        final String[] items = { "ab", "abc", "de", "df" };
        final FSMBuilder builder = new SimpleFSMBuilder(IConstants.FSM_DFA);
        builder.create(items);
        builder.printMachine();

        // TEST TRANSITION REMOVAL

        //		logger.info("Remove transition (4,5): ");
        //		FSM dfa = builder.getDFA();
        //		dfa.removeTransition(dfa.getTransition(4, 5));
        //		builder.printDFA();
        //		logger.info("Remove transition (2,3): ");
        //		dfa.removeTransition(dfa.getTransition(2, 3));
        //		builder.printDFA();

        // TEST STATE REMOVAL
        logger.info("Remove state 3: ");
        final FSM dfa = builder.getMachine();
        dfa.removeState(dfa.getState(3));
        builder.printMachine();

        logger.info("Remove state 4: ");
        dfa.removeState(dfa.getState(4));
        builder.printMachine();

        builder.dispose();
    }

    public static void testSimpleDFABuilder() {
        //		String[] inputs = {};
        //		String[] inputs = {"a"};
        //		String[] inputs = {"ab"};
        //		String[] inputs = {"a b"};
        //		String[] inputs = {"a b", "c d"};
        final String[] inputs = { "ab", "abc", "de", "df" };
        //		String[] inputs = {"ab", "a"};
        final FSMBuilder builder = new SimpleFSMBuilder(IConstants.FSM_DFA);
        builder.create(inputs);
        builder.printMachine();
        //		builder.create(IConstants.VIETNAMESE_LEXICON);
        //		builder.encode(IConstants.VIETNAMESE_LEXICON_DFA_SIMPLE);
        builder.dispose();
    }

    public static void testMinimalDFABuilder1() {
        //		String[] inputs = {"ab", "abc", "d", "dc"};
        //		String[] inputs = {"ab", "ac", "db", "dc"};
        final String[] inputs = { "bo", "ba" };
        // make sure that the input is sorted
        Arrays.sort(inputs, 0, inputs.length);
        final FSMBuilder builder = new MinimalFSMBuilder(IConstants.FSM_DFA);
        builder.create(inputs);
        builder.printMachine();
        builder.dispose();
    }

    /**
     * Build the minimal automaton representing the days of
     * a week.
     */
    public static void testMinimalDFABuilder2() {
        final FSMBuilder builder = new MinimalFSMBuilder(IConstants.FSM_DFA);
        //		builder.create("samples/days.txt");
        //		builder.create("samples/months.txt");
        //		builder.create("samples/aimer.txt");
        //		builder.create("samples/sample0.txt");
        //		builder.create("samples/sample1.txt");
        //		builder.printMachine();
        builder.create(IConstants.VIETNAMESE_LEXICON);
        //		builder.encode(IConstants.VIETNAMESE_LEXICON_DFA_MINIMAL);
        builder.dispose();
    }

    public static void testFSMUnmarshaller() {
        final FSMUnmarshaller unmarshaller = new FSMUnmarshaller();
        FSM dfa;
        //		dfa = unmarshaller.unmarshal("samples/months.xml");
        //		new FSMMarshaller().marshal(dfa, System.out);
        //		dfa = unmarshaller.unmarshal(IConstants.VIETNAMESE_LEXICON_DFA_SIMPLE, IConstants.FSM_DFA);
        dfa = unmarshaller.unmarshal(IConstants.VIETNAMESE_LEXICON_DFA_MINIMAL, IConstants.FSM_DFA);
        FSMUtilities.statistic(dfa);
    }

    public static void main(final String[] args) {
        //		testOperations();
        //		testSimpleDFABuilder();
        testMinimalDFABuilder1();
        //		testMinimalDFABuilder2();
        //		testFSMUnmarshaller();
    }
}
