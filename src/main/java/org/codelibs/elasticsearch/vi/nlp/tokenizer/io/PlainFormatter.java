/**
 * @author LE Hong Phuong
 * 8 sept. 2005
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.io;

import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author LE Hong Phuong
 *         <p>
 *         This class is an implementaton of interface
 *         <code>IOuputFormatter</code>. Its purpose is to provide a plain
 *         segmentation output, each token is kept on a seperate line.
 *
 */
public class PlainFormatter implements IOutputFormatter {

    /* (non-Javadoc)
     * @see vn.hus.tokenizer.io.IOutputFormatter#outputLexeme(vn.hus.tokenizer.tokens.LexerToken)
     */
    @Override
    public String outputLexeme(final TaggedWord token) {
        final StringBuffer stBuf = new StringBuffer();
        // output only the text of the token
        stBuf.append(token.getText());
        // end of line
        stBuf.append("\n");
        return stBuf.toString();
    }

}
