/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.fst;

import org.codelibs.elasticsearch.vi.nlp.fsm.FSM;
import org.codelibs.elasticsearch.vi.nlp.fsm.ISimulator;
import org.codelibs.elasticsearch.vi.nlp.fsm.Simulator;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Jan 24, 2008, 11:43:49 PM
 * <p>
 * Finite-state transducer.
 */
public class FST extends FSM {

    /**
     * Default constructor.
     */
    public FST() {
        super();
    }

    /* (non-Javadoc)
     * @see vn.hus.fsm.FSM#getSimulator()
     */
    @Override
    public ISimulator getSimulator() {
        return new FSTSimulator(this);
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
