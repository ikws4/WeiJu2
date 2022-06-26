package io.ikws4.codeeditor.language.lua;


import io.ikws4.codeeditor.api.language.LanguageParser;
import io.ikws4.codeeditor.api.language.ParseException;
import io.ikws4.codeeditor.api.language.ParseResult;

class LuaParser implements LanguageParser {
    private static LuaParser sInstance;

    private LuaParser() {}

    public static LuaParser getInstance() {
        if (sInstance == null) {
            sInstance = new LuaParser();
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
