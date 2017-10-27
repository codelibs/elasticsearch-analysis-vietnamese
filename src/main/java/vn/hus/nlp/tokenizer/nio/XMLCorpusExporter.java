/**
 *
 */
package vn.hus.nlp.tokenizer.nio;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import vn.hus.nlp.corpus.CorpusMarshaller;
import vn.hus.nlp.corpus.jaxb.Body;
import vn.hus.nlp.corpus.jaxb.Corpus;
import vn.hus.nlp.corpus.jaxb.ObjectFactory;
import vn.hus.nlp.corpus.jaxb.S;
import vn.hus.nlp.corpus.jaxb.W;
import vn.hus.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author LE HONG Phuong, phuonglh@gmail.com
 *         <p>
 *         Jul 13, 2009, 1:49:56 PM
 *         <p>
 *         An XML exporter for exporting tokenization results to XML format.
 *
 */
public class XMLCorpusExporter implements IExporter {

    private final CorpusMarshaller corpusMarshaller;

    public XMLCorpusExporter() {
        corpusMarshaller = new CorpusMarshaller();
    }

    /* (non-Javadoc)
     * @see vn.hus.nlp.tokenizer.nio.IExporter#export(java.util.List)
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
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}
