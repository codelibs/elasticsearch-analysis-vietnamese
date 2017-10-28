/**
 * (C) LE HONG Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.sd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import opennlp.maxent.DataStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jan 14, 2008, 10:15:18 PM
 *         <p>
 *         A data stream that reads data from an XML file in which Vietnamese
 *         sentences are surrounded by <s> tags. This stream uses a SAX parser
 *         to parse sentences.
 */
public class XMLDataStream implements DataStream {

    private static final Logger logger = LogManager.getLogger(XMLDataStream.class);

    List<String> dataStream;

    Iterator<String> dataStreamIterator;

    /**
     * @author LE HONG Phuong, phuonglh@gmail.com
     *         <p>
     *         Jan 14, 2008, 10:20:32 PM
     *         <p>
     *         The handler to read XML sentence file.
     */
    class XMLFileHandler extends DefaultHandler {
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            // take characters and create a string
            String s = new String(ch, start, length);
            // append it to the list of data streams
            if (s.trim().length() > 0) {
                // delete new line character of s if any
                s = s.replaceAll("[\\r\\n\\f]*", "");
                // delete redundants white space characters
                s = s.replaceAll("\\s+", " ");
                dataStream.add(s);
            }
        }

        @Override
        public void skippedEntity(final String name) throws SAXException {
            logger.info(name);
        }
    }

    /**
     * @param dataFile
     *            the XML data file
     */
    public XMLDataStream(final String dataFile) {
        // init the stream
        dataStream = new ArrayList<>();
        // init a SAX parser
        final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser;
        try {
            parser = parserFactory.newSAXParser();
            parser.parse(new File(dataFile), new XMLFileHandler());
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        // init the iterator
        dataStreamIterator = dataStream.iterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see opennlp.maxent.DataStream#hasNext()
     */
    @Override
    public boolean hasNext() {
        return dataStreamIterator.hasNext();
    }

    /*
     * (non-Javadoc)
     *
     * @see opennlp.maxent.DataStream#nextToken()
     */
    @Override
    public Object nextToken() {
        return dataStreamIterator.next();
    }

}
