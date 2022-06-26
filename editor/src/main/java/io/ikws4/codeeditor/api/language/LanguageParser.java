package io.ikws4.codeeditor.api.language;

public interface LanguageParser {

    /**
     *
     * @param name the language that need parsed
     * @param source source code
     * @return {@link ParseResult}
     */
    ParseResult parse(String name, String source);
}
