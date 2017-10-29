package org.codelibs.elasticsearch.vi.analysis;

import static org.apache.lucene.analysis.BaseTokenStreamTestCase.assertTokenStreamContents;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.codelibs.elasticsearch.vi.AnalysisVietnamesePlugin;
import org.codelibs.elasticsearch.vi.analysis.VietnameseAnalyzer;
import org.codelibs.elasticsearch.vi.analysis.VietnameseTokenizer;
import org.codelibs.elasticsearch.vi.analysis.VietnameseTokenizerFactory;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.CustomAnalyzer;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.test.ESTestCase;

/**
 * Created by duydo on 2/19/17.
 */
public class VietnameseAnalysisTest extends ESTestCase {

    public void testSimpleVietnameseAnalysis() throws IOException {
        final TestAnalysis analysis = createTestAnalysis();
        assertNotNull(analysis);

        final TokenizerFactory tokenizerFactory = analysis.tokenizer.get("vi_tokenizer");
        assertNotNull(tokenizerFactory);
        assertThat(tokenizerFactory, instanceOf(VietnameseTokenizerFactory.class));

        NamedAnalyzer analyzer = analysis.indexAnalyzers.get("vi_analyzer");
        assertNotNull(analyzer);
        assertThat(analyzer.analyzer(), instanceOf(VietnameseAnalyzer.class));

        analyzer = analysis.indexAnalyzers.get("my_analyzer");
        assertNotNull(analyzer);
        assertThat(analyzer.analyzer(), instanceOf(CustomAnalyzer.class));
        assertThat(analyzer.analyzer().tokenStream(null, new StringReader("")), instanceOf(VietnameseTokenizer.class));

    }


    public void testVietnameseTokenizer() throws IOException {
        final TestAnalysis analysis = createTestAnalysis();
        final TokenizerFactory tokenizerFactory = analysis.tokenizer.get("vi_tokenizer");
        assertNotNull(tokenizerFactory);

        final Tokenizer tokenizer = tokenizerFactory.create();
        assertNotNull(tokenizer);

        tokenizer.setReader(new StringReader("công nghệ thông tin Việt Nam"));
        assertTokenStreamContents(tokenizer, new String[]{"công nghệ thông tin", "Việt", "Nam"});
    }

    public void testVietnameseAnalyzer() throws IOException {
        final TestAnalysis analysis = createTestAnalysis();
        final NamedAnalyzer analyzer = analysis.indexAnalyzers.get("vi_analyzer");
        assertNotNull(analyzer);

        final TokenStream ts = analyzer.analyzer().tokenStream("test", "công nghệ thông tin Việt Nam");
        final CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        for (final String expected : new String[]{"công nghệ thông tin", "việt", "nam"}) {
            assertThat(ts.incrementToken(), equalTo(true));
            assertThat(term.toString(), equalTo(expected));
        }
        assertThat(ts.incrementToken(), equalTo(false));
    }

    public TestAnalysis createTestAnalysis() throws IOException {
        final String json = "/org/elasticsearch/index/analysis/vi_analysis.json";
        final Settings settings = Settings.builder()
                .loadFromStream(json, VietnameseAnalysisTest.class.getResourceAsStream(json))
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();
        final Settings nodeSettings = Settings.builder().put(Environment.PATH_HOME_SETTING.getKey(), createTempDir()).build();
        return createTestAnalysis(new Index("test", "_na_"), nodeSettings, settings, new AnalysisVietnamesePlugin());
    }
}
