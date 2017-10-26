/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.fsm.util;

import java.util.List;

import vn.hus.nlp.fsm.FSM;
import vn.hus.nlp.fsm.State;
import vn.hus.nlp.fsm.Transition;


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
		System.out.println("Some statistics about the machine: ");
		int maxOutTransitions = 0;
//		int maxInTransitions = 0;
		int nFinalStates = 0;
		for (final Integer integer : fsm.getStates().keySet()) {
			final State s = fsm.getStates().get(integer);
			if (s.isFinalState()) {
                nFinalStates++;
            }
			if (s.getNumberOfOutTransitions() > maxOutTransitions)
             {
                maxOutTransitions = s.getNumberOfOutTransitions();
//			Transition[] intransition = getIntransitions(fsm, s);
//			if (intransition != null && intransition.length > maxInTransitions)
//				maxInTransitions = intransition.length;
            }
		}
		System.out.println("\tNumber of states: " + fsm.getStates().size());
		System.out.println("\tNumber of final states: " + nFinalStates);
		System.out.println("\tNumber of transitions: " + fsm.getNTransitions());
		System.out.println("\tMaximum number of outtransitions = " + maxOutTransitions);
//		System.out.println("\tMaximum number of intransitions = " + maxInTransitions);
	}

}
