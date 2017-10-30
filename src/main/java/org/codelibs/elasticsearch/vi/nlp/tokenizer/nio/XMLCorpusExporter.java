/**
 *
 */
package org.codelibs.elasticsearch.vi.nlp.tokenizer.nio;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.elasticsearch.vi.nlp.corpus.CorpusMarshaller;
import org.codelibs.elasticsearch.vi.nlp.corpus.jaxb.Body;
import org.codelibs.elasticsearch.vi.nlp.corpus.jaxb.Corpus;
import org.codelibs.elasticsearch.vi.nlp.corpus.jaxb.ObjectFactory;
import org.codelibs.elasticsearch.vi.nlp.corpus.jaxb.S;
import org.codelibs.elasticsearch.vi.nlp.corpus.jaxb.W;
import org.codelibs.elasticsearch.vi.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jul 13, 2009, 1:49:56 PM
 *         <p>
 *         An XML exporter for exporting tokenization results to XML format.
 *
 */
public class XMLCorpusExporter implements IExporter {

    private static final Logger logger = LogManager.getLogger(XMLCorpusExporter.class);

    private final CorpusMarshaller corpusMarshaller;

    public XMLCorpusExporter() {
        corpusMarshaller = new CorpusMarshaller();
    }

    /* (non-Javadoc)
     * @see org.codelibs.elasticsearch.vi.nlp.tokenizer.nio.IExporter#export(java.util.List)
     */
    @Override
    public String export(final List<List<TaggedWord>> list) {
        final ObjectFactory factory = CorpusMarshaller.getFactory();
        final Corpus corpus = factory.createCorpus();
        corpus.setId(new Date().toString());
        final Body body = factory.createBody();
        corpus.setBody(body);

        final Iterator<List<TaggedWord>> iter = list.iterator();
        while (iter.hasNext()) {
            final List<TaggedWord> list2 = iter.next();
            if (list2.size() == 1 && list2.get(0).getText().equals("\n")) {
                body.getPOrS().add(factory.createP());
            } else {
                final S s = factory.createS();
                for (final TaggedWord tw : list2) {
                    final W w = factory.createW();
                    w.setContent(tw.getText());
                    w.setT(tw.getRule().getName());
                    s.getW().add(w);
                }
                body.getPOrS().add(s);
            }
        }

        final StringWriter writer = new StringWriter();

        try {
            corpusMarshaller.getMarshaller().marshal(corpus, writer);
            writer.close();
        } catch (final JAXBException e) {
            logger.warn(e);
        } catch (final IOException e) {
            logger.warn(e);
        }
        return writer.toString();
    }
}
