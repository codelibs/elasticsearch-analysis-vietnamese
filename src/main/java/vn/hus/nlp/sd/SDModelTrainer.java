/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.sd;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 15, 2008, 11:06:19 PM
 *         <p>
 *         This class trains a maxent model on an ensemble of pre-segmented
 *         sentences using a training corpus. The result of the training is a
 *         (binary and compressed) file which contains the trained model. The
 *         training corpus is a XML file with a simple schema in which each
 *         sentence is surrounded by a couple of tags <s> and </s>.
 */
public class SDModelTrainer
{

	// public static SentenceModel train(EventStream es, int iterations, int
	// cut)
	// throws IOException {
	//
	// return GIS.trainModel(es, iterations, cut);
	// }

	private static SentenceModel train(final InputStream corpus, final int iterations, final int cut) throws IOException
	{
		final ObjectStream<String> lineStream = new PlainTextByLineStream(corpus, Charset.forName("UTF-8"));

		final ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

		SentenceModel model;

		try
		{
			model = SentenceDetectorME.train("en", sampleStream, true, null, TrainingParameters.defaultParams());
		} finally
		{
			sampleStream.close();
		}

		return model;
	}

	/**
	 * Trains the maxent model using an XML data stream using an eos scanner.
	 *
	 * @param corpusFilename
	 *            the training corpus
	 * @param iterations
	 *            the number of iterations
	 * @param cut
	 *            the cut
	 * @return a SentenceModel
	 * @throws IOException
	 */
	public static SentenceModel train(final String corpusFilename, final int iterations, final int cut) throws IOException
	{
		return train(new FileInputStream(corpusFilename), iterations, cut);
	}

	/**
	 * Creates a model for a given language (french, vietnamese)
	 *
	 * @param language
	 *            the language to be used.
	 */
	public static void createModel(final String language, final File outputDirectory)
	{
		String trainingCorpus = "";
		String modelFilename = "";
		if (language.equalsIgnoreCase(IConstants.LANG_FRENCH))
		{
			trainingCorpus = IConstants.TRAINING_DATA_FRENCH;
			modelFilename = IConstants.MODEL_NAME_FRENCH;
		}
		if (language.equalsIgnoreCase(IConstants.LANG_VIETNAMESE))
		{
			trainingCorpus = IConstants.TRAINING_DATA_VIETNAMESE;
			modelFilename = IConstants.MODEL_NAME_VIETNAMESE;
		}
		try
		{
			System.err.println("Training the model on corpus: " + trainingCorpus);

			final ClassLoader cl = ClassLoader.getSystemClassLoader();
			final InputStream stream = cl.getResourceAsStream(trainingCorpus);

			// train the model, using 100 iterations and cutoff = 5
			final SentenceModel model = train(stream, 100, 5);

			// persist the model
			final File modelFile = new File(outputDirectory, modelFilename);
			System.err.println("Saving the model as: " + modelFile);

			OutputStream modelOut = null;
			try {
			  modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
			  model.serialize(modelOut);
			} finally {
			  if (modelOut != null) {
                modelOut.close();
            }
			}

		} catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(final String[] args)
	{
		if (args.length>0)
		{
			final String outputPath = args[0];

			final File output = new File(outputPath);
			if (output.exists())
			{
				try
				{
					FileUtils.forceMkdir(output);
				} catch (final IOException e)
				{
					System.err.println("Failed to create output directory.");
					System.exit(1);
				}
			}

			// create Vietnamese SD model
			SDModelTrainer.createModel(IConstants.LANG_VIETNAMESE, output);

			System.out.println("Done.");
		} else
		{
			System.err.println("Must specify output directory.");
		}

	}
}
