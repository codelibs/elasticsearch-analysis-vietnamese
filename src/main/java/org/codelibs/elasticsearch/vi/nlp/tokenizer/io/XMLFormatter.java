package org.codelibs.elasticsearch.vi.nlp.tokenizer.io;

import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 *
 * This class is an implementaton of interface <code>IOuputFormatter</code>.
 * Its purpose is to provide an XML output of the tokenization.
 * <p>
 *
 * @author Le Hong Phuong
 *
 */

public class XMLFormatter implements IOutputFormatter {

    /* (non-Javadoc)
     * @see vn.hus.tokenizer.io.IOutputFormatter#outputLexeme(vn.hus.tokenizer.tokens.LexerToken)
     */
    @Override
    public String outputLexeme(final TaggedWord lexeme) {
        final StringBuffer stBuf = new StringBuffer();
        stBuf.append("<w>");
        stBuf.append(lexeme.getText());
        stBuf.append("</w>\n");
        return stBuf.toString();

    }

}
