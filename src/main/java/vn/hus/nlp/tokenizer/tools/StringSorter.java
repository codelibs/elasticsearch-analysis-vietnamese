/* -- vnTokenizer 2.0 --
 *
 * Copyright information:
 *
 * LE Hong Phuong, NGUYEN Thi Minh Huyen,
 * Faculty of Mathematics Mechanics and Informatics,
 * Hanoi University of Sciences, Vietnam.
 *
 * Copyright (c) 2003
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the author.  The name of the author may not be used to
 * endorse or promote products derived from this software without
 * specific prior written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 *
 * Last update : 09/2006
 *
 */
package vn.hus.nlp.tokenizer.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.hus.nlp.tokenizer.ResourceHandler;

/**
 * @author LE Hong Phuong
 * <p> This class load Vietnamese syllables into an array of
 *         strings, sort these syllables and print out the result. This tool is
 *         used to prepare a sorted version of Vietnamese syllables that is used
 *         to construct a minimal DFA that recognizes Vietnamese syllables.
 *
 * @deprecated This tool is deprecated. To sort a Vietnamese text file, we use
 * the the utilities provided by VietPad project, see class {@link TextFileSorter}
 *
 *
 */
@Deprecated
public final class StringSorter {

	static final String ENCODING = "UTF-8";

	List<String> strings = null;

	public StringSorter() {
		strings = new ArrayList<>();
	}

	public StringSorter(final String dataFile) {
		strings = new ArrayList<>();
		loadDataFile(dataFile);
	}

	/**
	 * @param dataFile
	 */
	private void loadDataFile(final String dataFile) {
		// TODO Auto-generated method stub
		System.out.println("Loading the data file... Please wait....");
		try {
			// create a buffered reader to read the data file, line by line
			final FileInputStream fis = new FileInputStream(dataFile);
			final InputStreamReader isr = new InputStreamReader(fis,
					StringSorter.ENCODING);
			final BufferedReader br = new BufferedReader(isr);
			// now begin processing all lines of the data file
			String input = "";
			while ((input = br.readLine()) != null) {
				input = input.trim();
				if (input.length() > 0) {
					addInput(input);
					// System.out.println(input); // DEBUG
				}
			}
			br.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param input
	 */
	private void addInput(final String input) {
		// TODO Auto-generated method stub
		strings.add(input);
	}

	/**
	 * Sort the list
	 *
	 */
	public void sort() {
		Collections.sort(strings);
	}
	/**
	 * Write out the result to a file
	 * @param filename file name
	 */
	public void writeResult(final String filename) {
		System.out.println("Writing result... Please wait...");
		try {
			final FileOutputStream fos = new FileOutputStream(filename);
			final OutputStreamWriter writer = new OutputStreamWriter(fos,
					StringSorter.ENCODING);
			final BufferedWriter bwriter = new BufferedWriter(writer);
			for (final String s : strings) {
				bwriter.write(s);
				bwriter.write("\n");
			}
			bwriter.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		final String syllableFilename = ResourceHandler.get("wordDictionary");
		final StringSorter sorter = new StringSorter(syllableFilename);
		sorter.sort();
		final String syllableFilename2 = ResourceHandler.get("wordDictionary2");
		sorter.writeResult(syllableFilename2);
	}

}
