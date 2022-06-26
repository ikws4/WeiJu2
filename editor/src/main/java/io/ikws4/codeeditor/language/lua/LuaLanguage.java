package io.ikws4.codeeditor.language.lua;

import io.ikws4.codeeditor.api.language.Language;
import io.ikws4.codeeditor.api.language.LanguageParser;
import io.ikws4.codeeditor.api.language.LanguageSuggestionProvider;
import io.ikws4.codeeditor.api.language.LanguageStyler;

public class LuaLanguage implements Language {
    @Override
    public String getName() {
        return "java";
    }

    @Override
    public LanguageParser getParser() {
        return LuaParser.getInstance();
    }

    @Override
    public LanguageSuggestionProvider getSuggestionProvider() {
        return LuaSuggestionProvider.getInstance();
    }

    @Override
    public LanguageStyler getStyler() {
        return LuaStyler.getInstance();
    }
}
