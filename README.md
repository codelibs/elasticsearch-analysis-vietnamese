Vietnamese Analysis Plugin for Elasticsearch
========================================

Vietnamese Analysis plugin integrates Vietnamese language analysis into Elasticsearch.

The plugin provides the `vi_analyzer` analyzer and `vi_tokenizer` tokenizer. The `vi_analyzer` is composed of the `vi_tokenizer` tokenizer, the `lowercase` and `stop` filter.

## Version

[Versions in Maven Repository](http://central.maven.org/maven2/org/codelibs/elasticsearch-analysis-vi/)

## Installation

    $ $ES_HOME/bin/elasticsearch-plugin install org.codelibs:elasticsearch-analysis-vi:5.6.0


## Thanks to
- [Lê Hồng Phương](http://mim.hus.vnu.edu.vn/phuonglh/) for his VnTokenizer library
- [JetBrains](https://www.jetbrains.com) has provided a free license for their great tool: [IntelliJ IDEA](https://www.jetbrains.com/idea/)

