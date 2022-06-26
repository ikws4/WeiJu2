package io.ikws4.codeeditor.api.language;

import java.util.List;

public interface LanguageSuggestionProvider {
    List<Suggestion> getAll();

    void process(int line, String text);

    void delete(int line);

    void clear();
}
