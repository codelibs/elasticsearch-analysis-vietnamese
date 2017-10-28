/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.corpus;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import vn.hus.nlp.corpus.jaxb.ObjectFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * Unmarshaller of a corpus.
 */
public class CorpusMarshaller {

    private static final Logger logger = LogManager.getLogger(CorpusMarshaller.class);

    private JAXBContext jaxbContext;

    private Marshaller marshaller;

    static ObjectFactory factory = new ObjectFactory();

    /**
     * Default constructor.
     */
    public CorpusMarshaller() {
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
            logger.warn("JAXB Exeption", e);
        }
    }

    /**
     * Get the marshaller object.
     * @return the marshaller object
     */
    public Marshaller getMarshaller() {
        if (marshaller == null) {
            try {
                // create the marshaller
                marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            } catch (final JAXBException e) {
                logger.warn(e);
            }
        }
        return marshaller;
    }

    /**
     * Gets the factory.
     * @return the corpus factory.
     */
    public static ObjectFactory getFactory() {
        return factory;
    }
}
