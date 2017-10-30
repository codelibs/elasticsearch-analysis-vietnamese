/**
 *  @author LE Hong Phuong
 *  <p>
 *	23 mars 07
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.analysis.TaggedWordTokenizer;
import org.codelibs.elasticsearch.vi.nlp.sd.SentenceDetector;
import org.codelibs.elasticsearch.vi.nlp.sd.SentenceDetectorFactory;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.nio.XMLCorpusExporter;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;
import org.codelibs.elasticsearch.vi.nlp.utils.FileIterator;
import org.codelibs.elasticsearch.vi.nlp.utils.TextFileFilter;
import org.codelibs.elasticsearch.vi.nlp.utils.UTF8FileUtility;

/**
 * @author LE Hong Phuong
 * <p>
 * 23 mars 07
 * <p>
 * The main class of vnTokenizer.
 */
public final class VietTokenizer {

    private static final Logger logger = LogManager.getLogger(VietTokenizer.class);

    private static Tokenizer tokenizer = null;

    private static SentenceDetector sentenceDetector = null;

    private static boolean DEBUG = false;

    /**
     * Number of tokens procesed
     */
    private static int nTokens = 0;

    /**
     * Default constructor
     */
    public VietTokenizer() {
        tokenizer = TokenizerProvider.getInstance().getTokenizer();
        createSentenceDetector();
    }

    /**
     * Creates a tokenizer with parameters given in a properties file
     * @param propertiesFilename
     */
    public VietTokenizer(final String propertiesFilename) {
        tokenizer = TokenizerProvider.getInstance(propertiesFilename).getTokenizer();
        createSentenceDetector(propertiesFilename);
    }

    /**
     * Creates a tokenizer with parameters given in a properties object.
     * @param properties
     */
    public VietTokenizer(final Properties properties) {
        tokenizer = TokenizerProvider.getInstance(properties).getTokenizer();
        createSentenceDetector(properties);
    }

    /**
     * Creates a sentence detector.
     */
    private static void createSentenceDetector() {
        if (sentenceDetector == null) {
            sentenceDetector = SentenceDetectorFactory.create("vietnamese");
        }
    }

    /**
     * Creates a sentence detector.
     */
    private static void createSentenceDetector(final String propertiesFilename) {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFilename));
            createSentenceDetector(properties);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        } catch (final IOException e) {
            logger.warn(e);
        }
    }

    private static void createSentenceDetector(final Properties properties) {
        if (sentenceDetector == null) {
            sentenceDetector = SentenceDetectorFactory.create(properties);
        }
    }

    /**
     * A segment method, written for integration with other tools.
     * @param sentence a sentence to be segmented
     * @return a segmented sentence
     */
    public String segment(final String sentence) {
        final StringBuffer result = new StringBuffer(1000);
        final StringReader reader = new StringReader(sentence);
        // tokenize the sentence
        try {
            tokenizer.tokenize(reader);
            final List<TaggedWord> list = tokenizer.getResult();
            for (final TaggedWord taggedWord : list) {
                String word = taggedWord.toString();
                if (TokenizerOptions.USE_UNDERSCORE) {
                    word = word.replaceAll("\\s+", "_");
                } else {
                    word = "[" + word + "]";
                }
                result.append(word);
                result.append(' ');
            }
            // update nTokens
            nTokens += list.size();
            //
        } catch (final IOException e) {
            logger.warn(e);
        }
        return result.toString().trim();
    }

    /**
     * Tokenizes a text. If the option "use sentence detector" is on,
     * the sentence detector is called to segment the text into sentences; then
     * the tokenizer is used to tokenize detected sentences. If the option
     * is off, the text is directly tokenized by the tokenizer.
     * @param text a text to tokenize.
     * @return an array of tokenized sentences.
     * @see TokenizerOptions
     * @see TaggedWordTokenizer
     * @see SentenceDetector
     */
    public String[] tokenize(final String text) {
        final List<String> result = new ArrayList<>();
        final StringReader reader = new StringReader(text);
        if (TokenizerOptions.USE_SENTENCE_DETECTOR) {
            try {
                final String[] sentences = sentenceDetector.detectSentences(reader);
                for (final String sentence : sentences) {
                    // segment the sentence
                    result.add(segment(sentence));
                    //					// add an empty line
                    //					result.add("\n\n");
                }
            } catch (final IOException e) {
                logger.warn(e);
            }
        } else {
            // process all the text without detecting sentences
            result.add(segment(text));
        }
        // return the result
        return result.toArray(new String[result.size()]);
    }

    /**
     * Turns the sentence detector on.
     */
    public void turnOnSentenceDetection() {
        TokenizerOptions.USE_SENTENCE_DETECTOR = true;
    }

    /**
     * Turns the sentence detector off.
     */
    public void turnOffSentenceDetection() {
        TokenizerOptions.USE_SENTENCE_DETECTOR = false;
    }

    /**
     * Tokenizes an input file and write result to an output file.
     * These files are text files with UTF-8 encoding.
     * @param inputFile input file
     * @param outputFile output file
     */
    public void tokenize2(final String inputFile, final String outputFile) {
        tokenizer.tokenize(inputFile);
        tokenizer.exportResult(outputFile);
    }

    /**
     * Tokenizes an input file and write result to an output file.
     * These files are text files with UTF-8 encoding.
     * @param inputFile input file
     * @param outputFile output file
     */
    public void tokenize(final String inputFile, final String outputFile) {
        UTF8FileUtility.createWriter(outputFile);
        final String[] paragraphs = UTF8FileUtility.getLines(inputFile);

        if (!TokenizerOptions.XML_OUTPUT) {
            for (final String p : paragraphs) {
                final String[] sentences = tokenize(p);
                for (final String s : sentences) {
                    UTF8FileUtility.write(s.trim());
                    UTF8FileUtility.write("\n");
                }
            }
        } else { // XML outputer
            final List<List<TaggedWord>> list = new ArrayList<>();
            for (final String p : paragraphs) {
                try {
                    tokenizer.tokenize(new StringReader(p));
                } catch (final IOException e) {
                    logger.warn(e);
                }
                // make a copy of the result of tokenization
                final List<TaggedWord> result = new ArrayList<>(tokenizer.getResult());
                list.add(result);
                nTokens += result.size();
            }
            final String output = new XMLCorpusExporter().export(list);
            UTF8FileUtility.write(output);
        }
        UTF8FileUtility.closeWriter();
    }

    /**
     * Tokenizes all files in a directory.
     * @param inputDir an input dir
     * @param outputDir an output dir
     */
    public void tokenizeDirectory(final String inputDir, final String outputDir) {
        final TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
        final File inputDirFile = new File(inputDir);
        // get the current dir
        final String currentDir = new File(".").getAbsolutePath();
        final String inputDirPath = currentDir + File.separator + inputDir;
        final String outputDirPath = currentDir + File.separator + outputDir;

        if (DEBUG) {
            logger.info("currentDir = " + currentDir);
            logger.info("inputDirPath = " + inputDirPath);
            logger.info("outputDirPath = " + outputDirPath);
        }

        // get all input files
        final File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
        logger.info("Tokenizing all files in the directory, please wait...");
        final long startTime = System.currentTimeMillis();
        for (final File aFile : inputFiles) {
            // get the simple name of the file
            final String input = aFile.getName();
            // the output file have the same name with the automatic file
            final String output = outputDirPath + File.separator + input;
            // tokenize the file
            tokenize(aFile.getAbsolutePath(), output);
        }
        final long endTime = System.currentTimeMillis();
        final float duration = (float) (endTime - startTime) / 1000;
        logger.info("Tokenized " + nTokens + " words of " + inputFiles.length + " files in " + duration + " (s).\n");
    }

    /**
     * Gets the tokenizer.
     * @return the tokenizer
     */
    public static Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Gets the sentence detector.
     * @return the sentence detector.
     */
    public static SentenceDetector getSentenceDetector() {
        return sentenceDetector;
    }

}
