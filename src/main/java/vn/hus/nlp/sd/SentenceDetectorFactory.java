/**
 *
 */
package vn.hus.nlp.sd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 * <br>
 * May 25, 2009, 3:06:06 PM
 * <br>
 * A factory of the sentence detector. It purpose is to provide a concrete sentence detector
 * for a particular language, for example for French or for Vietnamese.
 */
public class SentenceDetectorFactory {

    private static final Logger logger = LogManager.getLogger(SentenceDetectorFactory.class);

    /**
     * Creates a sentence detector given a language.
     * @param language a language to use (french, vietnamese)
     * @return a sentence detector
     */
    public static SentenceDetector create(final String language) {
        String modelName = "";
        if (language.equalsIgnoreCase(IConstants.LANG_FRENCH)) {
            modelName = IConstants.MODEL_NAME_FRENCH;
        }
        if (language.equalsIgnoreCase(IConstants.LANG_VIETNAMESE)) {
            modelName = IConstants.MODEL_NAME_VIETNAMESE;
        }
        try {

            final InputStream stream = SentenceDetector.class.getResourceAsStream(modelName);

            return new SentenceDetector(stream);
        } catch (final IOException e) {
            logger.error("Error when constructing the sentence detector.");
            e.printStackTrace();
        }
        // return null in case of error
        return null;
    }

    /**
     * Creates a Vietnamese sent detector
     * @return a Vietnamese sent detector
     */
    public static SentenceDetector create(final Properties properties) {
        try {
            return new SentenceDetector(properties);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a Vietnamese sent detector
     * @return a Vietnamese sent detector
     */
    public static SentenceDetector create(final InputStream propertiesInputStream) {
        try {
            final Properties properties = new Properties();
            properties.load(propertiesInputStream);
            return new SentenceDetector(properties);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
