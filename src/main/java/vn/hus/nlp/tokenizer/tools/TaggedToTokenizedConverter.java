/**
 *
 */
package vn.hus.nlp.tokenizer.tools;

import java.io.File;

import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.TextFileFilter;
import vn.hus.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <br>
 * Jul 16, 2009, 8:08:12 PM
 * <br>
 * This utility is used to convert a tagged corpus to tokenized corpus.
 */
public class TaggedToTokenizedConverter {
	private static String TAGGED_FILE_EXTENSION = ".pos";
	private static String TOKENIZED_FILE_EXTENSION = ".txt";

	private TaggedToTokenizedConverter() {}

	/**
	 * Post process a string.
	 * @param string
	 * @return a processed string
	 */
	private static String postProcess(final String string) {
		// remove spaces before punctuations . , ! ? :
		String result = string;
		result = result.replaceAll("\\*E\\*", "");
		result = result.replaceAll("\\*T\\*", "");
		result = result.replaceAll("\\*E", "");
		result = result.replaceAll("E\\*", "");
		result = result.replaceAll("\\*T", "");
		result = result.replaceAll("T\\*", "");
		return result;
	}

	public static void convertFile(final String fileInp, final String fileOut) {
		final String[] taggedSents = UTF8FileUtility.getLines(fileInp);
		UTF8FileUtility.createWriter(fileOut);
		for (final String taggedSent : taggedSents) {
			final StringBuffer buffer = new StringBuffer();
			final String[] wts = taggedSent.split("\\s+");
			for (final String wt : wts) {
				final String[] pairs = wt.split("/");
				if (pairs.length > 0) {
					buffer.append(pairs[0]);
					buffer.append(" ");
				}
			}
			UTF8FileUtility.write(postProcess(buffer.toString().trim()) + "\n");
		}
		UTF8FileUtility.closeWriter();
	}


	public static void convertDirectory(final String dirInp, final String dirOut) {
		final TextFileFilter fileFilter = new TextFileFilter(TAGGED_FILE_EXTENSION);
		final File[] taggedFiles = FileIterator.listFiles(new File(dirInp), fileFilter);
		String filename = "";
		String fileOut = "";
		for (final File file : taggedFiles) {
			filename = file.getName();
			final int id = filename.indexOf('.');
			fileOut = (id > 0) ? dirOut + File.separator + filename.substring(0, id) + TOKENIZED_FILE_EXTENSION : filename + TOKENIZED_FILE_EXTENSION;
			convertFile(file.getAbsolutePath(), fileOut);
		}
		System.out.println("Converted " + taggedFiles.length + " files.");
	}

	public static void main(final String[] args) {
		// corpus 1
		final String dirInp = "data/VTB-20090712/VTB-20090712-POS";
		final String dirOut = "data/VTB-20090712/VTB-20090712-TOK";
		// corpus 2
//		String dirInp = "data/VTB-20090712/VTB-20090712-10K-POS";
//		String dirOut = "data/VTB-20090712/VTB-20090712-10K-TOK";
		convertDirectory(dirInp, dirOut);
	}
}
