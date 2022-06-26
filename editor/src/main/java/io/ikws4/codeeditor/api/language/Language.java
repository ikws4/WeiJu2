package io.ikws4.codeeditor.api.language;

public interface Language {
    /**
      * @return the language name
     */
    String getName();

    LanguageParser getParser();

    LanguageSuggestionProvider getSuggestionProvider();

    LanguageStyler getStyler();
}
