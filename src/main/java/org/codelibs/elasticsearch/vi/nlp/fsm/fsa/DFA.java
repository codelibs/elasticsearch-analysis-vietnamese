/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.fsa;

import org.codelibs.elasticsearch.vi.nlp.fsm.FSM;
import org.codelibs.elasticsearch.vi.nlp.fsm.ISimulator;
import org.codelibs.elasticsearch.vi.nlp.fsm.Simulator;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.fsm
 * <p>
 * Nov 7, 2007, 9:37:38 PM
 * <p>
 * Deterministic finite state automata.
 */
public class DFA extends FSM {

    /**
     * Default constructor of the DFA.
     */
    public DFA() {
        // init the state machine
        super();
    }

    /* (non-Javadoc)
     * @see vn.hus.fsm.FSM#getSimulator()
     */
    @Override
    public ISimulator getSimulator() {
        return new DFASimulator(this);
    }

    /* (non-Javadoc)
     * @see vn.hus.fsm.FSM#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        ((Simulator) getSimulator()).dispose();
    }

}
