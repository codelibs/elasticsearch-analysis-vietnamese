/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.sd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import vn.hus.nlp.utils.UTF8FileUtility;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 15, 2008, 11:50:28 PM
 *         <p>
 *         This is the general sentence detector for texts. It uses a maximum
 *         entropy model pretrained on an ensemble of texts. All the texts are supposed
 *         to be encoded in UTF-8 encoding.
 */
public class SentenceDetector extends SentenceDetectorME {

	/**
	 * Loads a new sentence detector using the model specified by the model
	 * name.
	 *
	 * @param modelName
	 *            The name of the maxent model trained for sentence detection.
	 * @throws IOException
	 *             If the model specified can not be read.
	 */
	public SentenceDetector(final String modelName) throws IOException {

		// NOTE: this may not be right!
		super(new SentenceModel(new File(modelName)));
	}

	public SentenceDetector(final InputStream stream) throws IOException
	{
		super(new SentenceModel(stream));
	}

	/**
	 * @param properties
	 * @throws IOException
	 */
	public SentenceDetector(final Properties properties) throws IOException {
		this(properties.getProperty("sentDetectionModel"));
//		System.out.println("Sentence detection model = " + properties.getProperty("sentDetectionModel"));
	}

	/**
	 * Performs sentence detection on a reader, returns an array of detected sentences.
	 * @param reader a reader
	 * @return an array of sentences
	 * @throws IOException
	 */
	public String[] detectSentences(final Reader reader) throws IOException {
		final BufferedReader bufReader = new BufferedReader(reader);
		final List<String> sentences = new ArrayList<>();
		for (String line = bufReader.readLine(); line != null; line = bufReader.readLine()) {
			if (line.trim().length() > 0) {
				// detect the sentences composing the line
				final String[] sents = sentDetect(line);
				// add them to the list of results
				for (final String s : sents) {
					sentences.add(s.trim());
				}
			}
		}
		// close the reader
		if (reader != null) {
            reader.close();
        }
		return sentences.toArray(new String[sentences.size()]);
	}

	/**
	 * Performs sentence detection a text file, returns an array of detected sentences.
	 * @param inputFile input file name
	 * @return an array of sentences
	 * @throws IOException
	 */
	public String[] detectSentences(final String inputFile) throws IOException {
		return detectSentences(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
	}

	public String[] detectSentences(final InputStream stream) throws IOException
	{
		return detectSentences(new InputStreamReader(stream, "UTF-8"));
	}

	/**
	 * Detects sentences of a text file, write results to an output file.
	 * @param inputFile an input file
	 * @param outputFile the result of the detection.
	 */
	public void detectSentences(final String inputFile, final String outputFile) {
		try {
			UTF8FileUtility.createWriter(outputFile);
			final String[] sentences = detectSentences(inputFile);
			for (final String s : sentences) {
				UTF8FileUtility.write(s + "\n");
			}
			UTF8FileUtility.closeWriter();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
