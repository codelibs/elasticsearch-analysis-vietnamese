/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.lexicon;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import vn.hus.nlp.lexicon.jaxb.Corpus;
import vn.hus.nlp.lexicon.jaxb.ObjectFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * A unmarshaller for lexicon.
 */
public class LexiconUnmarshaller {

    private static final Logger logger = LogManager.getLogger(LexiconUnmarshaller.class);

    JAXBContext jaxbContext;

    Unmarshaller unmarshaller;

    /**
     * Default constructor.
     */
    public LexiconUnmarshaller() {
        // create JAXB context
        //
        createContext();
    }

    private void createContext() {
        jaxbContext = null;
        try {
            final ClassLoader cl = ObjectFactory.class.getClassLoader();
            jaxbContext = JAXBContext.newInstance(IConstants.PACKAGE_NAME, cl);
        } catch (final JAXBException e) {
            logger.warn(e);
        }
    }

    /**
     * Get the marshaller object.
     * @return the marshaller object
     */
    protected Unmarshaller getUnmarshaller() {
        if (unmarshaller == null) {
            try {
                // create the unmarshaller
                unmarshaller = jaxbContext.createUnmarshaller();
            } catch (final JAXBException e) {
                logger.warn(e);
            }
        }
        return unmarshaller;
    }

    /**
     * Unmarshal a lexicon.
     * @param filename a lexicon file
     * @return a Corpus object.
     */
    public Corpus unmarshal(final String filename) {
        try {
            final InputStream stream = getClass().getResourceAsStream(filename);

            if (stream != null) {
                final Object object = getUnmarshaller().unmarshal(stream);
                if (object instanceof Corpus) {
                    final Corpus corpus = (Corpus) object;
                    return corpus;
                }
            }
        } catch (final JAXBException e) {
            logger.warn(e);
        }
        return null;
    }

}
