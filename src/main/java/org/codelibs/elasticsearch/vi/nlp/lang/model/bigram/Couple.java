/**
 *  @author LE Hong Phuong
 *  <p>
 *	17 mars 07
 */
package org.codelibs.elasticsearch.vi.nlp.lang.model.bigram;

/**
 *
 * @author LE Hong Phuong
 *         <p>
 *         13 mars 07
 *         </p>
 *         A couple of lexical tokens, used in bigram model. A couple can represent
 *         a coupe of words and its frequency and its probability.
 */
class Couple implements Comparable<Couple> {
    /**
     * First token
     */
    private final String first;

    /**
     * Second token
     */
    private final String second;
    /**
     * The frequency of a couple
     */
    private int freq;
    /**
     * The probability of a couple
     */
    private double prob;

    public Couple(final String f, final String s) {
        first = f;
        second = s;
        freq = 1;
        prob = 0;
    }

    /**
     * Get the first token.
     * @return
     */
    public String getFirst() {
        return first;
    }

    /**
     * Get the second token.
     * @return
     */
    public String getSecond() {
        return second;
    }

    /**
     * Return the fequency
     *
     * @return
     */
    public int getFreq() {
        return freq;
    }

    /**
     * Increase the frequency of this couple by one
     *
     * @return
     */
    public int incFreq() {
        freq += 1;
        return freq;
    }

    /**
     * Get the probability
     * @return
     */
    public double getProb() {
        return prob;
    }

    /**
     * Set the probability for the couple.
     * @param prob
     */
    public void setProb(final double prob) {
        this.prob = prob;
    }

    /**
     * Two couples are equal if the corresponding strings are equal (ignore
     * case).
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Couple)) {
            return false;
        }
        final Couple c = (Couple) o;
        return ((first.equalsIgnoreCase(c.first)) && (second.equalsIgnoreCase(c.second)));
    }

    /**
     * An important method for a good storage of couple inside a set. This
     * method is used by the <code>equals()</code> method to compare two
     * couples.
     */
    @Override
    public int hashCode() {
        return 3 * first.hashCode() + 5 * second.hashCode() + 7 * freq;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")" + "\t" + freq + "\t" + prob;
    }

    @Override
    public int compareTo(final Couple o) {
        final String fs = first + second;
        return fs.compareTo(o.getFirst() + o.getSecond());
    }

}
