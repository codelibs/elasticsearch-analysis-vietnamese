/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.FSM;
import org.codelibs.elasticsearch.vi.nlp.fsm.State;
import org.codelibs.elasticsearch.vi.nlp.fsm.Transition;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.fsm
 * <p>
 * Nov 9, 2007, 3:21:12 PM
 * <p>
 * Some utilities for querying information of a FSM.
 */
public final class FSMUtilities {

    private static final Logger logger = LogManager.getLogger(FSMUtilities.class);

    /**
     * Get an array of intransitions to a state of a machine.
     * @param fsm
     * @param s
     * @return intransitions of a state.
     */
    protected static Transition[] getIntransitions(final FSM fsm, final State s) {
        // get the intransition list of state s.
        final List<Transition> list = fsm.getIntransitionMap().get(s.getId());
        if (list != null) {
            return list.toArray(new Transition[list.size()]);
        } else {
            return null;
        }
    }

    /**
     * Give some simple statistics about a FSM.
     * @param fsm
     */
    public static void statistic(final FSM fsm) {
        logger.info("Some statistics about the machine: ");
        int maxOutTransitions = 0;
        //		int maxInTransitions = 0;
        int nFinalStates = 0;
        for (final Integer integer : fsm.getStates().keySet()) {
            final State s = fsm.getStates().get(integer);
            if (s.isFinalState()) {
                nFinalStates++;
            }
            if (s.getNumberOfOutTransitions() > maxOutTransitions) {
                maxOutTransitions = s.getNumberOfOutTransitions();
                //			Transition[] intransition = getIntransitions(fsm, s);
                //			if (intransition != null && intransition.length > maxInTransitions)
                //				maxInTransitions = intransition.length;
            }
        }
        logger.info("\tNumber of states: " + fsm.getStates().size());
        logger.info("\tNumber of final states: " + nFinalStates);
        logger.info("\tNumber of transitions: " + fsm.getNTransitions());
        logger.info("\tMaximum number of outtransitions = {}", maxOutTransitions);
        //		logger.info("\tMaximum number of intransitions = {}", maxInTransitions);
    }

}
