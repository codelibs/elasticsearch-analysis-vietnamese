/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.lang.model.test;

import java.io.IOException;

import vn.hus.nlp.lang.model.unigram.Unigram;
import vn.hus.nlp.lang.model.unigram.VocabularyBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Created: Apr 8, 2008, 1:53:51 PM
 * <p>
 * Test the vocabulary builder.
 */
public final class VocabularyBuilderTest {
	private static final Logger logger = LogManager.getLogger(VocabularyBuilderTest.class);

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// create an empty unigram model
		final Unigram unigram = Unigram.getInstance();
		// load the sample corpus to the unigram.
		try {
			Unigram.loadCorpus(TestConstants.CORPUS_NAME);
		} catch (final IOException e) {
			logger.warn(e);
		}
		// create the vocabulary of the model with the default cuttoff (1)
		VocabularyBuilder vocabularyBuilder = new VocabularyBuilder(unigram);
		// print the vocabulary
		vocabularyBuilder.print(TestConstants.VOCABULARY_1_TXT);

		// create another vocabulary of the model with a cutoff
		vocabularyBuilder = new VocabularyBuilder(unigram, 5);
		// print the vocabulary
		vocabularyBuilder.print(TestConstants.VOCABULARY_5_TXT);
	}

}
