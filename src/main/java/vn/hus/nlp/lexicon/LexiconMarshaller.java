/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.lexicon;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import vn.hus.nlp.lexicon.jaxb.Body;
import vn.hus.nlp.lexicon.jaxb.Corpus;
import vn.hus.nlp.lexicon.jaxb.ObjectFactory;
import vn.hus.nlp.lexicon.jaxb.W;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * Unmarshaller of a lexicon.
 */
public class LexiconMarshaller {

    private static final Logger logger = LogManager.getLogger(LexiconMarshaller.class);

    JAXBContext jaxbContext;

    Marshaller marshaller;

    /**
     * Default constructor.
     */
    public LexiconMarshaller() {
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
    protected Marshaller getMarshaller() {
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
     * Marshal a map of objects to a file.
     * @param filename the filename of the corpus. This file usually has extension .xml.
     */
    public void marshal(final Map<?, ?> map, final String filename) {
        // create the corpus object from the map
        //
        final ObjectFactory factory = new ObjectFactory();
        final Corpus corpus = factory.createCorpus();
        corpus.setId(filename);

        final Body body = factory.createBody();
        corpus.setBody(body);

        for (final Object key : map.keySet()) {
            final Object value = map.get(key);
            final W w = factory.createW();
            w.setContent(key.toString());
            w.setMsd(value.toString());
            body.getW().add(w);
        }
        // marshal the corpus
        //
        OutputStream os = null;
        try {
            os = new FileOutputStream(filename);
            getMarshaller().marshal(corpus, os);
        } catch (final FileNotFoundException e) {
            logger.warn(e);
        } catch (final JAXBException e) {
            logger.warn(e);
        }

    }

}
