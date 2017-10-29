/**
 * @author LE Hong Phuong
 * 8 sept. 2005
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.io;

import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author LE Hong Phuong
 *         <p>
 *         This class serves as an abstract output formatter of Vietnamese
 *         tokenization results.
 *
 */

public interface IOutputFormatter {
    /**
     * Output a token
     * @param token
     * @return a string representing the output of the token.
     */
    public String outputLexeme(TaggedWord token);
}
