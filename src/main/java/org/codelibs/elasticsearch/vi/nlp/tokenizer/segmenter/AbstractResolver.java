/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.segmenter;

import java.util.List;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * org.codelibs.elasticsearch.vi.nlp.segmenter
 * <p>
 * Nov 14, 2007, 9:40:11 PM
 * <p>
 * An abstract ambiguity resolver.
 */
public abstract class AbstractResolver {
    /**
     * @param segmentations a list of segmentations for a phrase.
     * @return the most probable segmentation
     */
    public abstract String[] resolve(List<String[]> segmentations);
}
