/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package org.codelibs.elasticsearch.vi.nlp.corpus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.corpus.jaxb.ObjectFactory;
import org.codelibs.elasticsearch.vi.nlp.lexicon.jaxb.Corpus;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * A unmarshaller of a corpus.
 */
public class CorpusUnmarshaller {

    private static final Logger logger = LogManager.getLogger(CorpusUnmarshaller.class);

    JAXBContext jaxbContext;

    Unmarshaller unmarshaller;

    /**
     * Default constructor.
     */
    public CorpusUnmarshaller() {
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
            final Object object = getUnmarshaller().unmarshal(new FileInputStream(filename));
            if (object instanceof Corpus) {
                final Corpus corpus = (Corpus) object;
                return corpus;
            }
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        } catch (final JAXBException e) {
            logger.warn(e);
        }
        return null;
    }

}
