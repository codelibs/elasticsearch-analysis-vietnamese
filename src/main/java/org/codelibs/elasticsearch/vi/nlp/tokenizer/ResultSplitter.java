/**
 * Phuong LE HONG, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.W;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.LexerRule;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <p>
 * Dec 24, 2009, 3:21:00 PM
 * <p>
 * This utility is used to split out a tagged token into two tokens. For example the
 * named entity "Ông Nông Đức Mạnh" is split into two tokens "Ông" and "Nông Đức Mạnh".
 */
public class ResultSplitter {

    /**
     * Set of predefined prefixes.
     */
    private final Set<String> prefix;

    /**
     * Default constructor.
     */
    public ResultSplitter() {
        this(IConstants.NAMED_ENTITY_PREFIX);
    }

    /**
     * Creates a result splitter from a named entity prefix filename.
     * @param namedEntityPrefixFilename
     */
    public ResultSplitter(final String namedEntityPrefixFilename) {
        // load the prefix lexicon
        //
        final LexiconUnmarshaller lexiconUnmarshaller = new LexiconUnmarshaller();
        final Corpus lexicon = lexiconUnmarshaller.unmarshal(namedEntityPrefixFilename);
        final List<W> ws = lexicon.getBody().getW();
        prefix = new HashSet<>();
        // add all prefixes to the set after converting them to lowercase
        for (final W w : ws) {
            prefix.add(w.getContent().toLowerCase());
        }
    }

    /**
     * Creates a result splitter from a properties filename.
     * @param properties a properties file.
     */
    public ResultSplitter(final Properties properties) {
        this(properties.getProperty("namedEntityPrefix"));
    }

    private boolean isPrefix(final String syllable) {
        return prefix.contains(syllable.toLowerCase());
    }

    /**
     * Splits a named entity token into two tokens.
     * @param token
     * @return two tagged tokens
     */
    public TaggedWord[] split(final TaggedWord token) {
        // split the token basing on spaces or underscore
        final String[] syllables = token.getText().split("\\s+");
        if (syllables.length > 1) {
            // extract the first syllable of token
            if (isPrefix(syllables[0])) {
                final int position = syllables[0].length() + 1;
                // it is sure that postion > 0
                final String suffix = token.getText().substring(position);
                final TaggedWord[] result = new TaggedWord[2];
                result[0] = new TaggedWord(new LexerRule("name:prefix"), syllables[0]);
                result[1] = new TaggedWord(new LexerRule("name"), suffix.trim());
                return result;
            }
        }
        return null;
    }
}
