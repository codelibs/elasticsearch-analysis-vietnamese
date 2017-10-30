package org.codelibs.elasticsearch.vi.nlp.lang.model.bigram;

/**
 *
 * @author LE Hong Phuong
 * <p>
 * 20 mars 07
 * <p>
 * vn.hus.tokenizer
 * <p>
 * Ambiguity group. The group contains a triple of tokens (in fact
 * these are three syllables).
 *
 */
public class Ambiguity {
    /**
     * First token
     */
    String first;
    /**
     * Second token
     */
    String second;
    /**
     * Third token
     */
    String third;
    /**
     * If <code>isFirstGroup</code> is <code>true</code>, the
     * solution (first,second) is chosen, the method <code>getSelection()</code>
     * will return the first two tokens, otherwise the last two tokens (second,third)
     * is chosen.
     */
    boolean isFirstGroup;

    public Ambiguity(final String f, final String s, final String t) {
        this.first = f;
        this.second = s;
        this.third = t;
        // default solution is (first,second) group.
        isFirstGroup = true;
    }

    /**
     * Update the <code>isFirstGroup</code> value.
     * @param b
     */
    public void setIsFirstGroup(final boolean b) {
        this.isFirstGroup = b;
    }

    /**
     * Get the selection
     * @return
     */
    public boolean getIsFirstGroup() {
        return isFirstGroup;
    }

    /**
     * Get a selection.
     * @return
     */
    public String[] getSelection() {
        final String[] firstGroup = { first, second };
        final String[] secondGroup = { second, third };
        if (isFirstGroup) {
            return firstGroup;
        } else {
            return secondGroup;
        }
    }

    @Override
    public int hashCode() {
        return 2 * first.hashCode() + 3 * second.hashCode() + 5 * third.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Ambiguity)) {
            return false;
        }
        final Ambiguity a = (Ambiguity) obj;
        return first.equalsIgnoreCase(a.first) && second.equalsIgnoreCase(a.second) && third.equalsIgnoreCase(a.third);
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + "," + third + ")";
    }

}
