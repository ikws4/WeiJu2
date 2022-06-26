package io.ikws4.codeeditor.api.language;

/**
 * A wrap class for {@link ParseException}
 */
public class ParseResult {
    private final ParseException mParseException;

    public ParseResult(ParseException parseException) {
        mParseException = parseException;
    }

    public String getMessage() {
        return mParseException.message;
    }

    public int getLine() {
        return mParseException.line;
    }

    public int getColumn() {
        return mParseException.column;
    }
}
