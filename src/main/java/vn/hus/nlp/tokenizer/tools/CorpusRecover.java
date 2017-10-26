/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 * vn.hus.tokenizer
 * 2007
 */
package vn.hus.nlp.tokenizer.tools;

import vn.hus.nlp.utils.UTF8FileUtility;


/**
 * @author LE Hong Phuong
 *         <p>
 *         23 mars 07
 *         </p>
 *         A recover for corpora, used to recover corpora from gold ones.
 *
 */
public final class CorpusRecover {

	/**
	 * Recover a corpus
	 *
	 * @param goldCorpus
	 *            a gold corpus filename
	 * @param corpus
	 *            a corpus
	 */
	public void recover(final String goldCorpus, final String corpus) {
		// load the gold corpus to an array
		final String[] lines = UTF8FileUtility.getLines(goldCorpus);
		final StringBuffer strBuffer = new StringBuffer();

		for (final String line : lines) {
			strBuffer.append(line);
			// add an new line character at the appropriate point.
			if (line.indexOf('.') >= 0 || line.indexOf('!') >= 0 || line.indexOf('?') >= 0) {
				strBuffer.append("\n");
			}
			strBuffer.append(" ");
		}
		// create a writer
		UTF8FileUtility.createWriter(corpus);
		// save the results
		UTF8FileUtility.write(strBuffer.toString());
		// close the writer
		UTF8FileUtility.closeWriter();
	}


	private static void usage() {
		System.out.println("\nUsage: CorpusRecover  <inputFile>  <outputFile>\n");
	}
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length != 2) {
			CorpusRecover.usage();
			System.exit(1);
		}
		new CorpusRecover().recover(args[0], args[1]);
		System.out.println("Done!");
	}

}
