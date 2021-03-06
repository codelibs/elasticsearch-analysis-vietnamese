package org.codelibs.elasticsearch.vi.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.W;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.ResultMerger;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.ResultSplitter;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.Segmenter;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.UnigramResolver;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.LexerRule;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

public class TaggedWordTokenizer {

    private static final Logger logger = LogManager.getLogger(TaggedWordTokenizer.class);

    private Segmenter segmenter;

    private ResultMerger resultMerger;

    private ResultSplitter resultSplitter;

    private final List<LexerRule> rules = new ArrayList<>();

    public TaggedWordTokenizer() {
        try (InputStream in = getClass().getResourceAsStream("/tokenizer.properties")) {
            final Properties properties = new Properties();
            properties.load(in);
            loadLexerRules(properties.getProperty("lexers"));
            resultMerger = new ResultMerger();
            resultSplitter = new ResultSplitter(properties);
            segmenter = new Segmenter(properties, new UnigramResolver(properties.getProperty("unigramModel")));
        } catch (final IOException e) {
            logger.warn(e);
        }
    }

    private void loadLexerRules(final String lexersFilename) {
        final LexiconUnmarshaller unmarshaller = new LexiconUnmarshaller();
        final Corpus corpus = unmarshaller.unmarshal(lexersFilename);
        final List<W> lexers = corpus.getBody().getW();
        for (final W w : lexers) {
            rules.add(new LexerRule(w.getMsd(), w.getContent()));
        }
    }

    public List<TaggedWord> tokenize(final Reader input) throws IOException {
        final List<TaggedWord> result = new ArrayList<>();
        final LineNumberReader reader = new LineNumberReader(input);
        String line = null;
        int column = 1;
        while (true) {
            if (line == null || line.trim().length() == 0) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
            }
            TaggedWord taggedWord = null;
            int tokenEnd = -1;
            int longestMatchLen = -1;
            LexerRule selectedRule = null;
            // find the rule that matches the longest substring of the input
            for (int i = 0; i < rules.size(); i++) {
                final LexerRule rule = rules.get(i);
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
                selectedRule = new LexerRule("phrase");
            }
            taggedWord = new TaggedWord(selectedRule, line.substring(0, endIndex), reader.getLineNumber(), column);
            // we match something, skip past the token, get ready
            // for the next match, and return the token
            column += endIndex;
            line = line.substring(endIndex).trim();

            // if this token is a phrase, we need to use a segmenter
            // object to segment it.
            if (taggedWord.isPhrase()) {
                final String phrase = taggedWord.getText().trim();
                if (phrase.contains(" ")) {
                    final String ruleName = taggedWord.getRule().getName();
                    String[] tokens = null;
                    // segment the phrase
                    final List<String[]> segmentations = new CopyOnWriteArrayList<>(segmenter.segment(phrase));
                    // resolved the result if there is such option
                    // and the there are many segmentations.
                    if (segmentations.size() > 1) {
                        tokens = segmenter.resolveAmbiguity(segmentations);
                    } else {
                        // get the first segmentation
                        final Iterator<String[]> it = segmentations.iterator();
                        if (it.hasNext()) {
                            tokens = it.next();
                        }
                    }

                    // build tokens of the segmentation
                    for (final String token : tokens) {
                        result.add(new TaggedWord(new LexerRule(ruleName), token, reader.getLineNumber(), column));
                        column += token.length();
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
        }

        return result.size() > 0 ? resultMerger.mergeList(result) : result;
    }
}
