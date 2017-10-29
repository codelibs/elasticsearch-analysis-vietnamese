/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.io;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.FSM;
import org.codelibs.elasticsearch.vi.nlp.fsm.IConstants;
import org.codelibs.elasticsearch.vi.nlp.fsm.State;
import org.codelibs.elasticsearch.vi.nlp.fsm.Transition;
import org.codelibs.elasticsearch.vi.nlp.fsm.fsa.DFA;
import org.codelibs.elasticsearch.vi.nlp.fsm.fst.FST;
import org.codelibs.elasticsearch.vi.nlp.fsm.jaxb.Fsm;
import org.codelibs.elasticsearch.vi.nlp.fsm.jaxb.ObjectFactory;
import org.codelibs.elasticsearch.vi.nlp.fsm.jaxb.S;
import org.codelibs.elasticsearch.vi.nlp.fsm.jaxb.States;
import org.codelibs.elasticsearch.vi.nlp.fsm.jaxb.T;
import org.codelibs.elasticsearch.vi.nlp.fsm.jaxb.Transitions;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.fsm
 * <p>
 * Nov 6, 2007, 10:32:24 PM
 * <p>
 */
public class FSMUnmarshaller {

    private static final Logger logger = LogManager.getLogger(FSMUnmarshaller.class);

    private JAXBContext jaxbContext;

    private Unmarshaller unmarshaller;

    /**
     * Default constructor.
     */
    public FSMUnmarshaller() {
        // create JAXB context
        //
        createContext();
    }

    private void createContext() {
        jaxbContext = null;
        try {
            final ClassLoader cl = ObjectFactory.class.getClassLoader();
            jaxbContext = JAXBContext.newInstance(IConstants.JAXB_CONTEXT, cl);
        } catch (final JAXBException e) {
            logger.warn(e);
        }
    }

    /**
     * Get the marshaller object.
     * @return the marshaller object.
     */
    public Unmarshaller getUnmarshaller() {
        if (unmarshaller == null) {
            try {
                // create the marshaller
                unmarshaller = jaxbContext.createUnmarshaller();
            } catch (final JAXBException e) {
                logger.warn(e);
            }
        }
        return unmarshaller;
    }

    /**
     * Unmarshal a fsm from a file.
     * @param filename a file.
     * @return a state machine
     */
    public FSM unmarshal(final String filename, final String machineType) {
        FSM fsm;
        if (machineType.equalsIgnoreCase(IConstants.FSM_DFA)) {
            fsm = new DFA();
        } else {
            fsm = new FST();
        }

        getUnmarshaller();
        try {

            final InputStream stream = getClass().getResourceAsStream(filename);

            final Object obj = unmarshaller.unmarshal(stream);
            if (obj != null) {
                final Fsm fsm2 = (Fsm) obj;
                // fill the states
                final States states = fsm2.getStates();
                for (final S s : states.getS()) {
                    final State state = new State(s.getId());
                    state.setType(s.getType());
                    fsm.addState(state);
                }
                // fill the transitions
                final Transitions transitions = fsm2.getTransitions();
                for (final T t : transitions.getT()) {
                    final char input = t.getInp().charAt(0);
                    final String output = t.getOut();
                    Transition transition;
                    if (output != null && output.equals(IConstants.EMPTY_STRING)) {
                        transition = new Transition(t.getSrc(), t.getTar(), input);
                    } else {
                        transition = new Transition(t.getSrc(), t.getTar(), input, output);
                    }
                    fsm.addTransition(transition);
                }

            }
        } catch (final JAXBException e) {
            logger.info("Error when unmarshalling the machine.");
            logger.warn(e);
        }
        return fsm;
    }

}
