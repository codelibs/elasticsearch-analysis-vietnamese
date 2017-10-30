/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.codelibs.elasticsearch.vi.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 * Vietnamese TaggedWordTokenizer.
 *
 * @author duydo
 */
public class VietnameseTokenizer extends Tokenizer {

    private final List<TaggedWord> pending = new CopyOnWriteArrayList<>();
    private int offset = 0;
    private int pos = 0;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

    private final TaggedWordTokenizer taggedWordTokenizer;
    private String inputText;

    public VietnameseTokenizer(final TaggedWordTokenizer taggedWordTokenizer) {
        super();
        this.taggedWordTokenizer = taggedWordTokenizer;
    }

    private void tokenize() throws IOException {
        inputText = IOUtils.toString(input);
        final List<TaggedWord> result = taggedWordTokenizer.tokenize(new StringReader(inputText));
        if (result != null) {
            pending.addAll(result);
        }
    }

    @Override
    public final boolean incrementToken() throws IOException {
        while (pending.isEmpty()) {
            tokenize();
            if (pending.isEmpty()) {
                return false;
            }
        }
        clearAttributes();

        for (int i = pos; i < pending.size(); i++) {
            pos++;
            final TaggedWord word = pending.get(i);
            if (accept(word)) {
                posIncrAtt.setPositionIncrement(1);
                final int length = word.getText().length();
                typeAtt.setType(String.format("<%s>", word.getRule().getName().toUpperCase()));
                termAtt.copyBuffer(word.getText().toCharArray(), 0, length);
                final int start = inputText.indexOf(word.getText(), i);
                offsetAtt.setOffset(correctOffset(start), offset = correctOffset(start + length));
                return true;
            }
        }
        return false;
    }

    /**
     * Only accept the word characters.
     */
    private final boolean accept(final TaggedWord word) {
        final String type = word.getRule().getName().toLowerCase();
        if ("punctuation".equals(type) || "special".equals(type)) {
            return false;
        }
        return true;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        final int finalOffset = correctOffset(offset);
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        pos = 0;
        offset = 0;
        pending.clear();
    }
}
