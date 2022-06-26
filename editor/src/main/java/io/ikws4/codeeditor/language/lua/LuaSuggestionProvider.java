package io.ikws4.codeeditor.language.lua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ikws4.codeeditor.api.language.LanguageSuggestionProvider;
import io.ikws4.codeeditor.api.language.Suggestion;
import io.ikws4.codeeditor.api.language.Suggestion.Type;

class LuaSuggestionProvider implements LanguageSuggestionProvider {
  private static LuaSuggestionProvider sInstance;
  private final Map<Integer, List<Suggestion>> mLineSuggestions = new HashMap<>();
  private final String[] mKeywords = ("and|break|do|else|elseif|" +
    "end|false|for|function|if|" +
    "in|local|nil|not|or|" +
    "repeat|return|then|true|until|while").split("\\|");

  private LuaSuggestionProvider() {
  }

  public static LuaSuggestionProvider getInstance() {
    if (sInstance == null) {
      sInstance = new LuaSuggestionProvider();
    }
    return sInstance;
  }

  @Override
  public List<Suggestion> getAll() {
    List<Suggestion> suggestions = new ArrayList<>();
    // keywords
    for (String keyword : mKeywords) {
      suggestions.add(new Suggestion(Type.KEYWORD, keyword));
    }
    for (List<Suggestion> value : mLineSuggestions.values()) {
      suggestions.addAll(value);
    }
    return suggestions;
  }

  @Override
  public void process(int line, String text) {
    List<Suggestion> list = mLineSuggestions.get(line);
    if (list == null) {
      list = new ArrayList<>();
    }

    // TODO: java suggestion provider process implementation
  }

  @Override
  public void delete(int line) {
    mLineSuggestions.remove(line);
  }

  @Override
  public void clear() {
    mLineSuggestions.clear();
  }
}
