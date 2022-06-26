package io.ikws4.codeeditor.api.language;

public class ParseException extends RuntimeException {
    public final String message;
    public final int line;
    public final int column;

    public ParseException(String message, int line, int column) {
        super(message);
        this.message = message;
        this.line = line;
        this.column = column;
    }
}
