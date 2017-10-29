/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.fsm.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.fsm.Configuration;
import org.codelibs.elasticsearch.vi.nlp.fsm.State;
import org.codelibs.elasticsearch.vi.nlp.fsm.Transition;
import org.codelibs.elasticsearch.vi.nlp.fsm.fsa.DFAConfiguration;
import org.codelibs.elasticsearch.vi.nlp.fsm.fst.FSTConfiguration;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Created: Jan 29, 2008, 1:50:33 PM
 * <p>
 * Builder of a simple machine. To build a minimal machine, use {@link MinimalFSMBuilder}.
 * @see {@link MinimalFSMBuilder}
 */
public class SimpleFSMBuilder extends FSMBuilder {

    private static final Logger logger = LogManager.getLogger(SimpleFSMBuilder.class);

    /**
     * Constructor.
     */
    public SimpleFSMBuilder(final String machineType) {
        super(machineType);
    }

    /* (non-Javadoc)
     * @see vn.hus.fsm.builder.FSMBuilder#addItem(java.lang.String, java.lang.String[])
     */
    @Override
    protected void addItem(final String input, final String[] output) {
        // track the item to find stop configuration. Note that
        // the track is based on only input and does not aware of
        // the output
        //
        final Configuration configuration = getSimulator().track(input);
        // get information about the configuration
        //
        State state = configuration.getCurrentState();
        String unprocessedInput = "";
        if (configuration instanceof DFAConfiguration) {
            final DFAConfiguration config = (DFAConfiguration) configuration;
            unprocessedInput = config.getUnprocessedInput();
        }
        if (configuration instanceof FSTConfiguration) {
            final FSTConfiguration config = (FSTConfiguration) configuration;
            unprocessedInput = config.getUnprocessedInput();
        }
        // There are two scenarios here: unprocessed input exists or not.
        if (unprocessedInput.length() > 0) {
            final int nState = machine.getStates().size();
            State newState = null;
            final boolean hasOutput = (output != null && output.length > 0);
            if (!hasOutput) {
                // if don't have output, we build FSA:
                //
                for (int i = 0; i < unprocessedInput.length(); i++) {
                    final char inp = unprocessedInput.charAt(i);
                    newState = new State(nState + i);
                    // add state
                    machine.addState(newState);
                    machine.addTransition(new Transition(state.getId(), newState.getId(), inp));
                    // update the current state
                    state = newState;
                }
            } else {
                if (output.length != input.length()) {
                    logger.error("Error! The output must have the same length with its input.");
                    System.exit(-1);
                }
                // have output, we build FST
                for (int i = 0; i < unprocessedInput.length(); i++) {
                    final char inp = unprocessedInput.charAt(i);
                    newState = new State(nState + i);
                    // add state
                    machine.addState(newState);
                    // there is output, we build a FST
                    final String out = output[output.length - unprocessedInput.length() + i];
                    machine.addTransition(new Transition(state.getId(), newState.getId(), inp, out));
                    // update the current state
                    state = newState;
                }
            }
            // the last added state is final
            newState.setType((byte) 2);
        } else {
            // the item has been already encoded in the machine.
            // We simply mark the last tracked state as final state.
            state.setType((byte) 2);
        }
    }

    /* (non-Javadoc)
     * @see vn.hus.fsm.builder.FSMBuilder#finalize()
     */
    @Override
    protected void finalize() {
        // do nothing
    }

}
