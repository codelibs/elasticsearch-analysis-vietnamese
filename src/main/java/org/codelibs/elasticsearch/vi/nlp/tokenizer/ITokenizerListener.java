/**
 *  @author LE Hong Phuong
 *  <p>
 *	22/01/2007
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer;

import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author LE Hong Phuong
 * <p>
 * 22/01/2007
 * <p>
 */
public interface ITokenizerListener {
    /**
     * Process a token
     * @param token
     */
    public void processToken(TaggedWord token);
}
