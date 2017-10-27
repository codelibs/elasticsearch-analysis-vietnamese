/**
 *
 */
package vn.hus.nlp.tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vn.hus.nlp.tokenizer.tokens.TaggedWord;

/**
 * @author phuonglh
 * <p>
 * This is a post-processor of vnTokeninzer. It corrects the tokenization result
 * by performing some fine-tuning operations, for example, mergence of dates.
 */
public class ResultMerger {

    private static String DAY_STRING_1 = "ngày";
    private static String DAY_STRING_2 = "Ngày";

    private static String MONTH_STRING_1 = "tháng";
    private static String MONTH_STRING_2 = "Tháng";

    private static String YEAR_STRING_1 = "năm";
    private static String YEAR_STRING_2 = "Năm";

    public ResultMerger() {

    }

    private TaggedWord mergeDateDay(final TaggedWord day, final TaggedWord nextToken) {
        TaggedWord taggedWord = null;
        if (nextToken.isDateDay()) {
            final String text = day.getText() + " " + nextToken.getText();
            taggedWord = new TaggedWord(nextToken.getRule(), text, nextToken.getLine(), day.getColumn());
        }
        return taggedWord;
    }

    private TaggedWord mergeDateMonth(final TaggedWord month, final TaggedWord nextToken) {
        TaggedWord taggedWord = null;
        if (nextToken.isDateMonth()) {
            final String text = month.getText() + " " + nextToken.getText();
            taggedWord = new TaggedWord(nextToken.getRule(), text, nextToken.getLine(), month.getColumn());
        }
        return taggedWord;
    }

    private TaggedWord mergeDateYear(final TaggedWord year, final TaggedWord nextToken) {
        TaggedWord taggedWord = null;
        // merge the date year or a number
        if (nextToken.isDateYear() || nextToken.isNumber()) {
            final String text = year.getText() + " " + nextToken.getText();
            taggedWord = new TaggedWord(nextToken.getRule(), text, nextToken.getLine(), year.getColumn());
        }
        return taggedWord;
    }

    /**
     * @param token
     * @param nextToken
     * @return a lexer token merging from two tokens or <tt>null</tt>.
     */
    private TaggedWord mergeDate(final TaggedWord token, final TaggedWord nextToken) {
        if (token.getText().equals(DAY_STRING_1) || token.getText().equals(DAY_STRING_2)) {

            return mergeDateDay(token, nextToken);
        }
        if (token.getText().equals(MONTH_STRING_1) || token.getText().equals(MONTH_STRING_2)) {
            return mergeDateMonth(token, nextToken);
        }
        if (token.getText().equals(YEAR_STRING_1) || token.getText().equals(YEAR_STRING_2)) {
            return mergeDateYear(token, nextToken);
        }
        return null;
    }

    /**
     * Merge the result of the tokenization.
     * @param tokens
     * @return a list of lexer tokens
     */
    public List<TaggedWord> mergeList(final List<TaggedWord> tokens) {
        final List<TaggedWord> result = new ArrayList<>();
        TaggedWord token = new TaggedWord(""); // a fake start token
        final Iterator<TaggedWord> it = tokens.iterator();
        while (it.hasNext()) {
            // get a token
            final TaggedWord nextToken = it.next();
            // try to merge the two tokens
            final TaggedWord mergedToken = mergeDate(token, nextToken);
            // if they are merged
            if (mergedToken != null) {
                //				System.out.println(mergedToken.getText()); // DEBUG
                result.remove(result.size() - 1);
                result.add(mergedToken);
            } else { // if they aren't merge
                result.add(nextToken);
            }
            token = nextToken;
        }
        return result;
    }
}
