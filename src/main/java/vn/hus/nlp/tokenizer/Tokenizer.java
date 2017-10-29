package vn.hus.nlp.tokenizer;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vn.hus.nlp.fsm.IConstants;
import vn.hus.nlp.lexicon.LexiconUnmarshaller;
import vn.hus.nlp.lexicon.jaxb.Corpus;
import vn.hus.nlp.lexicon.jaxb.W;
import vn.hus.nlp.tokenizer.io.Outputer;
import vn.hus.nlp.tokenizer.segmenter.Segmenter;
import vn.hus.nlp.tokenizer.tokens.LexerRule;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;
import vn.hus.nlp.tokenizer.tokens.WordToken;
import vn.hus.nlp.utils.UTF8FileUtility;

/**
 * @author LE Hong Phuong, phuonglh@gmail.com
 * <p>
 * The Vietnamese tokenizer.
 */

public class Tokenizer {

    /**
     * List of rules for this lexer
     */
    private LexerRule rules[] = new LexerRule[0];

    /**
     * The current input stream
     */
    private InputStream inputStream;

    /**
     * Current reader, keep track of our position within the input file
     */
    private LineNumberReader lineReader;

    /**
     * Current line
     */
    private String line;

    /**
     * Current column
     */
    private int column;

    /**
     * A list of tokens containing the result of tokenization
     */
    private List<TaggedWord> result = null;

    /**
     * A lexical segmenter
     */
    private final Segmenter segmenter;
    /**
     * A lexer token outputer
     */
    private Outputer outputer = null;
    /**
     * A list of tokenizer listeners
     */
    private final List<ITokenizerListener> tokenizerListener = new ArrayList<>();
    /**
     * Are ambiguities resolved? True by default.
     */
    private boolean isAmbiguitiesResolved = true;

    private Logger logger;

    private final ResultMerger resultMerger;

    private final ResultSplitter resultSplitter;

    /**
     * Creates a tokenizer from a lexers filename and a segmenter.
     * @param lexersFilename the file that contains lexer rules
     * @param segmenter a lexical segmenter<ol></ol>
     */
    public Tokenizer(final String lexersFilename, final Segmenter segmenter) {
        // load the lexer rules
        loadLexerRules(lexersFilename);
        this.segmenter = segmenter;
        result = new ArrayList<>();
        // use a plain (default) outputer
        createOutputer();
        // create result merger
        resultMerger = new ResultMerger();
        // create a result splitter
        resultSplitter = new ResultSplitter();
        // create logger
        createLogger();
        // add a simple tokenizer listener for reporting
        // tokenization progress
        addTokenizerListener(new SimpleProgressReporter());
    }

    /**
     * Creates a tokenizer from a properties object and a segmenter. This is
     * the prefered way to create a tokenizer.
     * @param properties
     * @param segmenter
     */
    public Tokenizer(final Properties properties, final Segmenter segmenter) {
        // load the lexer rules
        loadLexerRules(properties.getProperty("lexers"));
        this.segmenter = segmenter;
        result = new ArrayList<>();
        // use a plain (default) outputer
        createOutputer();
        // create result merger
        resultMerger = new ResultMerger();
        // create a result splitter
        resultSplitter = new ResultSplitter(properties);
        // create logger
        createLogger();
        // add a simple tokenizer listener for reporting
        // tokenization progress
        addTokenizerListener(new SimpleProgressReporter());
    }

    private void createOutputer() {
        if (outputer == null) {
            outputer = new Outputer();
        }
    }

    /**
     * @return an outputer
     */
    public Outputer getOutputer() {
        return outputer;
    }

    /**
     * @param outputer The outputer to set.
     */
    public void setOutputer(final Outputer outputer) {
        this.outputer = outputer;
    }

    private void createLogger() {
        if (logger == null) {
            logger = Logger.getLogger(Segmenter.class.getName());
            // use a console handler to trace the log
            //			logger.addHandler(new ConsoleHandler());
            try {
                logger.addHandler(new FileHandler("tokenizer.log"));
            } catch (final SecurityException e) {
                logger.log(Level.WARNING, "Security Exception" + e.getMessage());
            } catch (final IOException e) {
                logger.log(Level.WARNING, "IO Exception" + e.getMessage());
            }
            logger.setLevel(Level.FINEST);
        }
    }

    /**
     * Load lexer specification file. This text file contains lexical rules to
     * tokenize a text
     *
     * @param lexersFilename
     *            specification file
     */
    private void loadLexerRules(final String lexersFilename) {
        final LexiconUnmarshaller unmarshaller = new LexiconUnmarshaller();
        final Corpus corpus = unmarshaller.unmarshal(lexersFilename);
        final ArrayList<LexerRule> ruleList = new ArrayList<>();
        final List<W> lexers = corpus.getBody().getW();
        for (final W w : lexers) {
            final LexerRule lr = new LexerRule(w.getMsd(), w.getContent());
            ruleList.add(lr);
        }
        // convert the list of rules to an array and save it
        rules = ruleList.toArray(rules);
    }

    /**
     * Tokenize a reader. If ambiguities are not resolved, this method
     * selects the first segmentation for a phrase if there are more than one
     * segmentations. Otherwise, it selects automatically the most
     * probable segmentation returned by the ambiguity resolver.
     */
    public void tokenize(final Reader reader) throws IOException {
        // Firstly, the result list is emptied
        result.clear();
        lineReader = new LineNumberReader(reader);
        // position within the file
        line = null;
        column = 1;
        // do tokenization
        while (true) {
            // get the next token
            final TaggedWord taggedWord = getNextToken();
            // stop if there is no more token
            if (taggedWord == null) {
                break;
            }
            // if this token is a phrase, we need to use a segmenter
            // object to segment it.
            if (taggedWord.isPhrase()) {
                final String phrase = taggedWord.getText().trim();
                if (!isSimplePhrase(phrase)) {
                    final String ruleName = taggedWord.getRule().getName();
                    String[] tokens = null;
                    // segment the phrase
                    final List<String[]> segmentations = segmenter.segment(phrase);
                    if (segmentations.size() == 0) {
                        logger.log(Level.WARNING, "The segmenter cannot segment the phrase \"" + phrase + "\"");
                    }
                    // resolved the result if there is such option
                    // and the there are many segmentations.
                    if (isAmbiguitiesResolved() && segmentations.size() > 1) {
                        tokens = segmenter.resolveAmbiguity(segmentations);
                    } else {
                        // get the first segmentation
                        final Iterator<String[]> it = segmentations.iterator();
                        if (it.hasNext()) {
                            tokens = it.next();
                        }
                    }
                    if (tokens == null) {
                        logger.log(Level.WARNING, "Problem: " + phrase);
                    }

                    // build tokens of the segmentation
                    for (final String token2 : tokens) {
                        final WordToken token = new WordToken(new LexerRule(ruleName), token2, lineReader.getLineNumber(), column);
                        result.add(token);
                        column += token2.length();
                    }
                } else { // phrase is simple
                    if (phrase.length() > 0) {
                        result.add(taggedWord);
                    }
                }
            } else { // lexerToken is not a phrase
                // check to see if it is a named entity
                if (taggedWord.isNamedEntity()) {
                    // try to split the lexer into two lexers
                    final TaggedWord[] tokens = resultSplitter.split(taggedWord);
                    if (tokens != null) {
                        for (final TaggedWord token : tokens) {
                            result.add(token);
                        }
                    } else {
                        result.add(taggedWord);
                    }
                } else {
                    // we simply add it into the list
                    if (taggedWord.getText().trim().length() > 0) {
                        result.add(taggedWord);
                    }
                }
            }
            // ok, the token has been processed,
            // it is now reported to all registered listeners
            fireProcess(taggedWord);
        }
        // close the line reader
        if (lineReader != null) {
            lineReader.close();
        }
        // merge the result
        result = resultMerger.mergeList(result);
    }

    /**
     * Tokenize a file.
     *
     * @param filename
     *            a filename
     */
    public void tokenize(final String filename) {
        // create a UTF8 reader
        UTF8FileUtility.createReader(filename);
        try {
            tokenize(UTF8FileUtility.reader);
        } catch (final IOException e) {
            logger.log(Level.WARNING, "IO Exception" + e.getMessage());
        }
        UTF8FileUtility.closeReader();
    }

    /**
     * A phrase is simple if it contains only one syllable.
     * @param phrase
     * @return true/false
     */
    private boolean isSimplePhrase(String phrase) {
        phrase = phrase.trim();
        if (phrase.indexOf(IConstants.BLANK_CHARACTER) >= 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Return the next token from the input. Version 2, less greedy
     * method than version 1.
     *
     * @return next token from the input
     * @throws IOException
     */
    private TaggedWord getNextToken() throws IOException {
        // scan the file line by line and quit when no more lines are left
        if (line == null || line.length() == 0) {
            line = lineReader.readLine();
            if (line == null) {
                if (inputStream != null) {
                    inputStream.close();
                }
                lineReader = null;
                return null;
            }
            // an empty line corresponds to an empty tagged word
            if (line.trim().length() == 0) {

                logger.log(Level.WARNING, "Create an empty line tagged word...");
                //return new TaggedWord(new LexerRule("return", "(\\^\\$)"), "\n");
                return new TaggedWord(new LexerRule("return"), "\n");
            }
            column = 1;
        }
        // match the next token
        TaggedWord token = null;
        // the end of the next token, within the line
        int tokenEnd = -1;
        // the length of the rule that matches the most characters from the
        // input
        int longestMatchLen = -1;
        final int lineNumber = lineReader.getLineNumber();
        LexerRule selectedRule = null;
        // find the rule that matches the longest substring of the input
        for (final LexerRule rule : rules) {
            // get the precompiled pattern for this rule
            final Pattern pattern = rule.getPattern();
            // create a matcher to perform match operations on the string
            // by interpreting the pattern
            final Matcher matcher = pattern.matcher(line);
            // find the longest match from the start
            if (matcher.lookingAt()) {
                final int matchLen = matcher.end();
                if (matchLen > longestMatchLen) {
                    longestMatchLen = matchLen;
                    tokenEnd = matchLen;
                    selectedRule = rule;
                }
            }
        }
        //
        // check if this relates to an email address (to fix an error with email)
        // yes, I know that this "manual" method must be improved by a more general way.
        // But at least, it can fix an error with email addresses at the moment. :-)
        int endIndex = tokenEnd;
        if (tokenEnd < line.length()) {
            if (line.charAt(tokenEnd) == '@') {
                while (endIndex > 0 && line.charAt(endIndex) != ' ') {
                    endIndex--;
                }
            }
        }
        // the following statement fixes the error reported by hiepnm, for the case like "(School@net)"
        if (endIndex == 0) {
            endIndex = tokenEnd;
        }

        if (selectedRule == null) {
            selectedRule = new LexerRule("word");
        }
        final String text = line.substring(0, endIndex);
        token = new TaggedWord(selectedRule, text, lineNumber, column);
        // we match something, skip past the token, get ready
        // for the next match, and return the token
        column += endIndex;
        line = line.substring(endIndex).trim();
        return token;
    }

    /**
     * Export the result of tokenization to a text file, the output
     * format is determined by an outputer
     *
     * @param filename a file to export the result to
     * @param outputer an outputer
     * @see vn.hus.nlp.tokenizer.io.IOutputFormatter
     */
    public void exportResult(final String filename, final Outputer outputer) {
        logger.info("Exporting result of tokenization...");
        try (final FileOutputStream fos = new FileOutputStream(filename);
             final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
             final BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(outputer.output(result));
            bw.flush();
        } catch (final IOException e) {
            logger.log(Level.WARNING, "IO Exception" + e.getMessage());
        }
        logger.info("OK.");
    }

    /**
     * Export the result of tokenization to a text file using the plain output
     * format.
     * @param filename
     */
    public void exportResult(final String filename) {
        logger.info("Exporting result of tokenization...");
        UTF8FileUtility.createWriter(filename);
        for (final TaggedWord token : result) {
            UTF8FileUtility.write(token.toString() + "\n");
        }
        UTF8FileUtility.closeWriter();
        logger.info("OK");
    }

    /**
     * @return Returns the a result of tokenization.
     */
    public List<TaggedWord> getResult() {
        return result;
    }

    /**
     * @param result
     *            The result to set.
     */
    public void setResult(final List<TaggedWord> result) {
        this.result = result;
    }

    /**
     * Adds a listener
     * @param listener a listener to add
     */
    public void addTokenizerListener(final ITokenizerListener listener) {
        tokenizerListener.add(listener);
    }

    /**
     * Removes a tokenier listener
     * @param listener a listener to remove
     */
    public void removeTokenizerListener(final ITokenizerListener listener) {
        tokenizerListener.remove(listener);
    }

    /**
     * Get all the tokenizer listener
     * @return a list of listeners
     */
    public List<ITokenizerListener> getTokenizerListener() {
        return tokenizerListener;
    }

    /**
     * Reports process of the tokenization to all listener
     * @param token the processed token
     */
    private void fireProcess(final TaggedWord token) {
        for (final ITokenizerListener listener : tokenizerListener) {
            listener.processToken(token);
        }
    }

    /**
     * Dispose the tokenizer
     *
     */
    public void dispose() {
        // dispose the segmenter
        segmenter.dispose();
        // clear all lexer tokens
        result.clear();
        // remove all tokenizer listeners
        tokenizerListener.clear();
    }

    /**
     * Return <code>true</code> if the ambiguities are resolved by
     * a resolver. The default value is <code>false</code>.
     * @return
     */
    public boolean isAmbiguitiesResolved() {
        return isAmbiguitiesResolved;
    }

    /**
     * Is the ambiguity resolved?
     * @param b <code>true/false</code>
     */
    public void setAmbiguitiesResolved(final boolean b) {
        isAmbiguitiesResolved = b;
    }

    /**
     * Return the lexical segmenter
     * @return
     */
    public Segmenter getSegmenter() {
        return segmenter;
    }

    /**
     * @author Le Hong Phuong, phuonglh@gmail.com
     * <p>
     * 8 juil. 2009, 22:57:19
     * <p>
     * A simple listener for reporting tokenization progress.
     */
    private class SimpleProgressReporter implements ITokenizerListener {

        @Override
        public void processToken(final TaggedWord token) {
            // report some simple progress
            if (result.size() % 1000 == 0) {
                logger.info(".");
            }
        }

    }
}
