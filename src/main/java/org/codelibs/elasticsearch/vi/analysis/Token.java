package org.codelibs.elasticsearch.vi.analysis;

public class Token {
    private String type;
    private String text;
    private int startOffset = -1;
    private int endOffset = -1;
    private int pos = -1;

    public Token(final String type, final String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(final int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(final int endOffset) {
        this.endOffset = endOffset;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(final int pos) {
        this.pos = pos;
    }
}
