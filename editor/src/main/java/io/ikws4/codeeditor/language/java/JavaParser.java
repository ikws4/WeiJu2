package io.ikws4.codeeditor.language.java;


import io.ikws4.codeeditor.api.language.LanguageParser;
import io.ikws4.codeeditor.api.language.ParseException;
import io.ikws4.codeeditor.api.language.ParseResult;

class JavaParser implements LanguageParser {
    private static JavaParser sInstance;

    private JavaParser() {}

    public static JavaParser getInstance() {
        if (sInstance == null) {
            sInstance = new JavaParser();
        }
        return sInstance;
    }

    @Override
    public ParseResult parse(String name, String source) {
        // TODO: Implement java parser
        ParseException exception = new ParseException("Unable to parse unsupported language", 0, 0);
        return new ParseResult(exception);
    }
}
