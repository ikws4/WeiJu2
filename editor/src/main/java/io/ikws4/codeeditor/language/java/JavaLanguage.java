package io.ikws4.codeeditor.language.java;

import io.ikws4.codeeditor.api.language.Language;
import io.ikws4.codeeditor.api.language.LanguageParser;
import io.ikws4.codeeditor.api.language.LanguageSuggestionProvider;
import io.ikws4.codeeditor.api.language.LanguageStyler;

public class JavaLanguage implements Language {
    @Override
    public String getName() {
        return "java";
    }

    @Override
    public LanguageParser getParser() {
        return JavaParser.getInstance();
    }

    @Override
    public LanguageSuggestionProvider getSuggestionProvider() {
        return JavaSuggestionProvider.getInstance();
    }

    @Override
    public LanguageStyler getStyler() {
        return JavaStyler.getInstance();
    }
}
