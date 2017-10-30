/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 * vn.hus.tokenizer
 * 2007
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.AbstractResolver;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.Segmenter;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter.UnigramResolver;

/**
 * @author LE Hong Phuong
 *         <p>
 *         8 janv. 07
 *         </p>
 *         A provider of tokenizer. It creates a tokenizer for Vietnamese.
 */
public final class TokenizerProvider {

    private static final Logger logger = LogManager.getLogger(TokenizerProvider.class);

    /**
     * A lexical segmenter
     */
    private Segmenter segmenter;
    /**
     * An ambiguity resolver
     */
    private AbstractResolver resolver;
    /**
     * The tokenizer
     */
    private Tokenizer tokenizer;
    /**
     * An instance flag
     */
    private static boolean instanceFlag = false;

    private static TokenizerProvider provider;

    /**
     * Private constructor
     */
    private TokenizerProvider() {
        final Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/tokenizer.properties"));
            // create a unigram resolver.
            resolver = new UnigramResolver(properties.getProperty("unigramModel"));
            // create a lexical segmenter that use the unigram resolver
            segmenter = new Segmenter(properties, resolver);
            // init the tokenizer
            tokenizer = new Tokenizer(properties, segmenter);
            // Do not resolve the ambiguity.
            //			tokenizer.setAmbiguitiesResolved(false);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        } catch (final IOException e) {
            logger.warn(e);
        }
    }

    private TokenizerProvider(final String propertiesFilename) {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(propertiesFilename));
            // create a unigram resolver.
            //
            resolver = new UnigramResolver(properties.getProperty("unigramModel"));
            // create a lexical segmenter that use the unigram resolver
            segmenter = new Segmenter(properties, resolver);
            // init the tokenizer
            tokenizer = new Tokenizer(properties, segmenter);
            // Do not resolve the ambiguity.
            //			tokenizer.setAmbiguitiesResolved(false);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        } catch (final IOException e) {
            logger.warn(e);
        }
    }

    private TokenizerProvider(final Properties properties) {
        // create a unigram resolver.
        resolver = new UnigramResolver(properties.getProperty("unigramModel"));
        // create a lexical segmenter that use the unigram resolver
        segmenter = new Segmenter(properties, resolver);
        // init the tokenizer
        tokenizer = new Tokenizer(properties, segmenter);
        // Do not resolve the ambiguity.
        //		tokenizer.setAmbiguitiesResolved(false);
    }

    /**
     * Instantiate a tokenizer provider object.
     *
     * @return a provider object
     */
    public static TokenizerProvider getInstance() {
        if (!instanceFlag) {
            instanceFlag = true;
            provider = new TokenizerProvider();
        }
        return provider;
    }

    /**
     * Instantiate a tokenizer provider object, parameters are read from a properties file.
     *
     * @return a provider object
     */
    public static TokenizerProvider getInstance(final String propertiesFilename) {
        if (!instanceFlag) {
            instanceFlag = true;
            provider = new TokenizerProvider(propertiesFilename);
        }
        return provider;
    }

    /**
     * Instantiate a tokenizer provider object, parameters are read from a properties object.
     *
     * @return a provider object
     */
    public static TokenizerProvider getInstance(final Properties properties) {
        if (!instanceFlag) {
            instanceFlag = true;
            provider = new TokenizerProvider(properties);
        }
        return provider;
    }

    /**
     * Get the lexical segmenter
     *
     * @return the lexical segmenter
     */
    public Segmenter getSegmenter() {
        return segmenter;
    }

    /**
     * Returns the tokenizer
     *
     * @return
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Dispose the data provider
     */
    public void dispose() {
        // dispose the tokenizer
        // this will dispose the lexical tokenizer and the automata
        tokenizer.dispose();
    }
}
