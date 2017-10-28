/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 *  Vietnam National University, Hanoi, Vietnam.
 */
package vn.hus.nlp.tokenizer.segmenter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * vn.hus.nlp.segmenter
 * <p>
 * Nov 15, 2007, 11:51:08 PM
 * <p>
 * An accent normalizer for Vietnamese string. The purpose of
 * this class is to convert a syllable like "hòa" to "hoà",
 * since the lexicon contains only the later form.
 */
public final class StringNormalizer {

    private static final Logger logger = LogManager.getLogger(StringNormalizer.class);

    private static Map<String, String> map;

    private StringNormalizer(final String mapFile) {
        map = new HashMap<>();
        init(mapFile);
    }

    private void init(final String mapFile) {

        final InputStream stream = getClass().getResourceAsStream(mapFile);
        List<String> rules;
        try {
            rules = IOUtils.readLines(stream, "UTF-8");

            for (int i = 0; i < rules.size(); i++) {
                final String rule = rules.get(i);

                final String[] s = rule.split("\\s+");
                if (s.length == 2) {
                    map.put(s[0], s[1]);
                } else {
                    logger.error("Wrong syntax in the map file " + mapFile + " at line " + i);
                }
            }

        } catch (final IOException e) {
            // TODO Auto-generated catch block
            logger.warn(e);
        }

    }

    /**
     * @return an instance of the class.
     */
    public static StringNormalizer getInstance() {
        return new StringNormalizer(IConstants.NORMALIZATION_RULES);
    }

    /**
     * @param properties
     * @return an instance of the class.
     */
    public static StringNormalizer getInstance(final Properties properties) {
        return new StringNormalizer(properties.getProperty("normalizationRules"));
    }

    /**
     * Normalize a string.
     * @return a normalized string
     * @param s a string
     */
    public String normalize(final String s) {
        String result = new String(s);
        for (final String from : map.keySet()) {
            final String to = map.get(from);
            if (result.indexOf(from) >= 0) {
                result = result.replace(from, to);
            }
        }
        return result;
    }

}
