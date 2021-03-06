/**
 *  @author LE Hong Phuong
 *  <p>
 *	17 mars 07
 */
package org.codelibs.elasticsearch.vi.nlp.lang.model.bigram;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.lang.model.IConstants;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconMarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.LexiconUnmarshaller;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.W;

/**
 * @author LE Hong Phuong
 *         <p>
 *         17 mars 07
 *         <p>
 *         vn.hus.tokenizer
 *         <p>
 *         This is the estimator for lambda values in the smoothed bigram model.
 *         The estimator calculates conditional probabilities of the bigram
 *         model and uses them to estimate lambda values. The lambda values and
 *         conditional probabilities will be used by a resolver to resolve
 *         ambiguities of a segmentation.
 *
 */
public class Estimator {

    private static final Logger logger = LogManager.getLogger(Estimator.class);

    /**
     * Epsilon value
     */
    private static double EPSILON = 0.01;

    private double lambda1;

    private double lambda2;

    /**
     * Probabilities P(w_i) = P(s)
     */
    private Map<String, Integer> unigram;

    /**
     * Probabilities P(w_{i-1}, w_i) = P(f, s)
     */
    private Map<Couple, Integer> bigram;

    /**
     * Conditional probabilities P(w_i | w_{i-1}) = P(s | f)
     */
    private List<Couple> probabilities;

    private LexiconUnmarshaller unmarshaller;

    private LexiconMarshaller marshaller;

    private Map<String, List<Couple>> tokenMap;

    /**
     * Construct an estimator given data files.
     *
     * @param unigramDataFile
     * @param bigramDataFile
     */
    public Estimator(final String unigramDataFile, final String bigramDataFile) {
        init();
        loadModels(unigramDataFile, bigramDataFile);
    }

    /**
     * Initialize the maps.
     *
     */
    private void init() {
        // init the collections
        unigram = new HashMap<>();
        bigram = new HashMap<>();
        probabilities = new ArrayList<>();
        // create the unmarshaller
        unmarshaller = new LexiconUnmarshaller();
        // create the marshaller
        marshaller = new LexiconMarshaller();

        //
        tokenMap = new HashMap<>();
    }

    /**
     * Find all couples in the bigram model that has the first string is
     * <code>first</code>.
     *
     * @param first
     *            a string
     * @return
     */
    private Couple[] findFirst(final String first) {
        final List<Couple> couples = tokenMap.get(first);
        if (couples != null) {
            return couples.toArray(new Couple[couples.size()]);
        } else {
            return null;
        }
    }

    /**
     * Estimate conditional probabilities P(s | f), for all f and s in the
     * model. This method is called once to estimate conditional probabilities.
     * The result is saved to a file by method <code>outputConditionalProbabilities</code>.
     *
     */
    private void estimateConditionalProb() {
        logger.info("Estimating conditional probabilities...");
        final Iterator<String> firstIt = unigram.keySet().iterator();
        while (firstIt.hasNext()) {
            final String first = firstIt.next();
            // get the c(first)
            final int cFirst = unigram.get(first); // auto-unboxing
            // find all couple(first, second) in the bigram given the first
            final Couple[] biCouples = findFirst(first);
            if (biCouples != null) {
                // create probabilities P(s | f) and add them to the conditional
                // probabilities list
                for (final Couple biCouple : biCouples) {
                    final Couple c = new Couple(biCouple.getSecond(), first);
                    final double prob = (double) (biCouple.getFreq()) / cFirst;
                    c.setProb(prob);
                    probabilities.add(c);
                }
            }
        }
        logger.info("Sorting the probability list...");
        // sort the list of conditional probs
        // using the couple comparator
        Collections.sort(probabilities, new CoupleComparator());
    }

    /**
     * Estimates lambda values
     *
     */
    private void estimate() {
        // calculate conditional probabilities
        estimateConditionalProb();
        logger.info("Estimating lambda values...");

        final long beginTime = System.currentTimeMillis();

        lambda1 = 0.5;
        lambda2 = 0.5;

        double hatEpsilon = 0;
        double hatLambda1 = 0;
        double hatLambda2 = 0;
        double c1 = 0;
        double c2 = 0;

        // number of loops
        int m = 0;

        do {
            hatLambda1 = lambda1;
            hatLambda2 = lambda2;
            // calculate c1 and c2
            c1 = 0;
            c2 = 0;
            final Iterator<Couple> biTokens = bigram.keySet().iterator();
            while (biTokens.hasNext()) {
                final Couple couple = biTokens.next();
                final String first = couple.getFirst(); // w_{i-1}
                final String second = couple.getSecond(); // w_i
                // calculate the denominator
                final double denominator = (lambda1 * getConditionalProbability(second, first) + lambda2 * getUnigramProbability(second));
                //				logger.info("denominator = {}", denominator);
                //				logger.info("getConditionalProbability(second, first) = " + getConditionalProbability(second, first));
                //				logger.info("getUnigramProbability(second) = " + getUnigramProbability(second));
                if (denominator > 0) {
                    // calculate c1
                    c1 += (couple.getFreq() * lambda1 * getConditionalProbability(second, first)) / denominator;
                    // calculate c2
                    c2 += (couple.getFreq() * lambda2 * getUnigramProbability(second)) / denominator;
                    //					logger.info("c1 = {} c2 = {}", c1, c2);
                }
            }
            // re-estimate lamda1 and lambda2
            lambda1 = c1 / (c1 + c2);
            validateProbabilityValue(lambda1);
            lambda2 = 1 - lambda1;
            hatEpsilon = Math.sqrt((lambda1 - hatLambda1) * (lambda1 - hatLambda1) + (lambda2 - hatLambda2) * (lambda2 - hatLambda2));
            logger.info("m = {}", m);
            logger.info("lambda1 = {}", lambda1);
            logger.info("lambda2 = {}", lambda2);
            logger.info("hatEpsilon = {}", hatEpsilon);
            // inc number of loops
            m++;
            if (m > 10) {
                break;
            }
        } while (hatEpsilon > EPSILON);
        final long endTime = System.currentTimeMillis();
        logger.info("Executed time (ms) = " + (endTime - beginTime));
        logger.info("Loop terminated!");
        logger.info("m = {}", m);
        logger.info("lambda1 = {}", lambda1);
        logger.info("lambda2 = {}", lambda2);
        logger.info("hatEpsilon = {}", hatEpsilon);

    }

    /**
     * Validate a probability value (between 0 and 1)
     * @param prob
     */
    private void validateProbabilityValue(final double prob) {
        if ((prob < 0) || (prob > 1)) {
            logger.error("Error! Invalid probability!");
        }
    }

    /**
     * Get the probability of a token in the unigram model P(w_i)
     * @param token
     * @return
     */
    private double getUnigramProbability(final String token) {
        if (unigram.keySet().contains(token)) {
            return (double) (unigram.get(token).intValue()) / getTokenCount();
        }
        // if the token is not in the bigram model
        return 0;
    }

    /**
     * Get the conditional probability of a couple of tokens.
     * @param first
     * @param second
     * @return
     */
    private double getConditionalProbability(final String first, final String second) {
        return getConditionalProbability(new Couple(first, second));
    }

    /**
     * Get the conditional probability of a couple
     * @param couple
     * @return
     */
    private double getConditionalProbability(final Couple couple) {
        /*
        Iterator<Couple> couples = probabilities.iterator();
        while (couples.hasNext()) {
        	Couple c = couples.next();
        	if (c.equals(couple)) return c.getProb();
        }
        // there does not the couple in the probabilities
        return 0;
        */
        final int index = Collections.binarySearch(probabilities, couple, new CoupleComparator());
        if (index >= 0) {
            return probabilities.get(index).getProb();
        }
        return 0;
    }

    /**
     * Load data files and fill unigram and bigram models.
     *
     * @param unigramDataFile
     * @param bigramDataFile
     */
    private void loadModels(final String unigramDataFile, final String bigramDataFile) {
        logger.info("Loading models...");
        // load unigram model
        final Corpus unigramCorpus = unmarshaller.unmarshal(unigramDataFile);
        List<W> ws = unigramCorpus.getBody().getW();
        for (final W w : ws) {
            final String freq = w.getMsd();
            final String word = w.getContent();
            unigram.put(word, Integer.parseInt(freq));
        }
        // load bigram model
        // and initialize the token map
        final Corpus bigramCorpus = unmarshaller.unmarshal(bigramDataFile);
        ws = bigramCorpus.getBody().getW();
        for (final W w : ws) {
            final String freq = w.getMsd();
            final String words = w.getContent();
            // split the word using a comma.
            // In general, there are only 2 words, but if a word itself is a
            // comma, we simply do not consider this case :-)
            final String[] two = words.split(",");

            if (two.length == 2) {
                // update the bigram model
                final String first = two[0];
                final String second = two[1];
                // create a couple
                final Couple couple = new Couple(first, second);
                // put the couple to the bigram
                bigram.put(couple, Integer.parseInt(freq));
                // update the token map
                //
                List<Couple> secondTokens = tokenMap.get(first);
                if (secondTokens == null) {
                    secondTokens = new ArrayList<>();
                    secondTokens.add(couple);
                    tokenMap.put(first, secondTokens);
                } else {
                    secondTokens.add(couple);
                }
            }
        }
        logger.info("tokenMap's size = " + tokenMap.size());
    }

    /**
     * Get the number of tokens in the training corpus.
     * This value can be calculated using the unigram model.
     * It is then used to calculate probabilities P(w_i) and P(w_{i-1}, w_i).
     *
     * @return number of tokens in the training corpus.
     */
    private int getTokenCount() {
        int n = 0;
        final Iterator<String> token = unigram.keySet().iterator();
        while (token.hasNext()) {
            final String t = token.next();
            n += (unigram.get(t).intValue());
        }
        return n;
    }

    /**
     * Output conditional probabilities P(s | f) to an XML file.
     * This file will be used as data file for a resolver <code>Resolver</code>.
     *
     * @param filename
     *            a file
     */
    private void marshalConditionalProbabilities(final String filename) {
        logger.info("Marshalling conditional probabilities...");
        // prepare a map for marshalling
        final Map<String, String> map = new HashMap<>();
        logger.info("probabilities's size = " + probabilities.size());
        String key;
        String value;
        final DecimalFormat decimalFormat = new DecimalFormat("#.000");
        for (final Couple c : probabilities) {
            key = c.getFirst() + "|" + c.getSecond();
            value = decimalFormat.format(c.getProb());
            map.put(key, value);
        }
        // marshal the map
        marshaller.marshal(map, filename);

    }

    /**
     * Output conditional probabilities P(s | f) to a plain text file.
     * This file will be used as data file for a resolver <code>Resolver</code>.
     *
     * @param filename
     *            a file
     * @deprecated
     */
    @Deprecated
    void outputConditionalProbabilities(final String filename) {
        try (final FileOutputStream outputStream = new FileOutputStream(filename);
                final Writer writer = new OutputStreamWriter(outputStream, "UTF-8");
                final BufferedWriter bufWriter = new BufferedWriter(writer)) {

            // write the result
            final Iterator<Couple> couples = probabilities.iterator();
            while (couples.hasNext()) {
                final Couple c = couples.next();
                bufWriter.write("(" + c.getFirst() + "|" + c.getSecond() + ")" + "\t" + c.getProb());
                bufWriter.write("\n");
            }
            // flush the writer
            bufWriter.flush();
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        } catch (final IOException e) {
            logger.warn(e);
        }
    }

    /**
     * Estimate conditional probabilities and marshal the result to a file.
     */
    void buildConditionalProbabilities() {
        estimateConditionalProb();
        //		estimator.outputConditionalProbabilities(IConstants.CONDITIONAL_PROBABILITIES);
        marshalConditionalProbabilities(IConstants.CONDITIONAL_PROBABILITIES);

    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Estimator estimator = new Estimator(IConstants.UNIGRAM_MODEL, IConstants.BIGRAM_MODEL);
        //		estimator.buildConditionalProbabilities();
        estimator.estimate();
        logger.info("Done");
    }

}
