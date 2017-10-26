/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.fsm.test;

import vn.hus.nlp.fsm.IConstants;
import vn.hus.nlp.fsm.builder.FSMBuilder;
import vn.hus.nlp.fsm.builder.MinimalFSMBuilder;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Created: Jan 29, 2008, 4:15:21 PM
 * <p>
 * Test the construction of a minimal FST.
 */
public class MinimalFSTClient {


	public static void test1() {
		final String[] inputs = {"ba"};

//		String[][] outputs = {{"b", "a"}};
//		String[][] outputs = {{"m", "e"}};
		final String[][] outputs = {{"ong", "ba"}};

		final FSMBuilder  builder = new MinimalFSMBuilder(IConstants.FSM_FST);
		builder.create(inputs, outputs);
		builder.printMachine();
		builder.dispose();
	}

	public static void test2() {
		final String[] inputs = {"ba", "bo"};

//		String[][] outputs = {{"b", "a"}, {"b", "o"}};
		final String[][] outputs = {{"ba", "ma"}, {"co", "chu"}};

		final FSMBuilder  builder = new MinimalFSMBuilder(IConstants.FSM_FST);
		builder.create(inputs, outputs);
		builder.printMachine();
		builder.dispose();
	}



	public static void main(final String[] args) {
//		test1();
		test2();
	}
}
