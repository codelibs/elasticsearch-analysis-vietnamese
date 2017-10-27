/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.corpus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import vn.hus.nlp.corpus.jaxb.ObjectFactory;
import vn.hus.nlp.lexicon.jaxb.Corpus;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 * <p>
 * A unmarshaller of a corpus.
 */
public class CorpusUnmarshaller {

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
            e.printStackTrace();
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
                e.printStackTrace();
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
            e.printStackTrace();
        } catch (final JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
