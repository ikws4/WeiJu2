package io.ikws4.weiju.editor;

import android.os.Bundle;

import androidx.annotation.NonNull;

import org.eclipse.tm4e.core.theme.IRawTheme;

import java.io.InputStream;
import java.io.Reader;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionHelper;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

class TextMateLanguage extends EmptyLanguage {

    private TextMateAnalyzer textMateAnalyzer;
    private int tabSize = 4;
    private final IdentifierAutoComplete autoComplete = new IdentifierAutoComplete();
    boolean autoCompleteEnabled;
    final boolean createIdentifiers;

    private TextMateLanguage(String grammarName, InputStream grammarIns, Reader languageConfiguration, IRawTheme theme, boolean createIdentifiers) {
        try {
            textMateAnalyzer = new TextMateAnalyzer(this, grammarName, grammarIns, languageConfiguration, theme);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.createIdentifiers = createIdentifiers;
        autoCompleteEnabled = true;
    }

    public static TextMateLanguage create(String grammarName, InputStream grammarIns, Reader languageConfiguration, IRawTheme theme) {
        return new TextMateLanguage(grammarName, grammarIns, languageConfiguration, theme, true);
    }

    public static TextMateLanguage create(String grammarName, InputStream grammarIns, IRawTheme theme) {
        return new TextMateLanguage(grammarName, grammarIns, null, theme, true);
    }

    public static TextMateLanguage createNoCompletion(String grammarName, InputStream grammarIns, Reader languageConfiguration, IRawTheme theme) {
        return new TextMateLanguage(grammarName, grammarIns, languageConfiguration, theme, false);
    }

    public static TextMateLanguage createNoCompletion(String grammarName, InputStream grammarIns, IRawTheme theme) {
        return new TextMateLanguage(grammarName, grammarIns, null, theme, false);
    }

    /**
     * When you update the {@link TextMateColorScheme} for editor, you need to synchronize the updates here
     *
     * @param theme IRawTheme creates from file
     */
    public void updateTheme(IRawTheme theme) {
        if (textMateAnalyzer != null) {
            textMateAnalyzer.updateTheme(theme);
        }
    }

    public void setKeywords(String[] keywords, boolean lowCase) {
        autoComplete.setKeywords(keywords, lowCase);
    }

    @NonNull
    @Override
    public AnalyzeManager getAnalyzeManager() {
        if (textMateAnalyzer != null) {
            return textMateAnalyzer;
        }
        return EmptyLanguage.EmptyAnalyzeManager.INSTANCE;
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * Set tab size. The tab size is used to compute code blocks.
     */
    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    public int getTabSize() {
        return tabSize;
    }

    @Override
    public SymbolPairMatch getSymbolPairs() {
        return new SymbolPairMatch.DefaultSymbolPairs();
    }

    public boolean isAutoCompleteEnabled() {
        return autoCompleteEnabled;
    }

    public void setAutoCompleteEnabled(boolean autoCompleteEnabled) {
        this.autoCompleteEnabled = autoCompleteEnabled;
    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position, @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        if (!autoCompleteEnabled) {
            return;
        }
        var prefix = CompletionHelper.computePrefix(content, position, MyCharacter::isJavaIdentifierPart);
        final var idt = textMateAnalyzer.syncIdentifiers;
        autoComplete.requireAutoComplete(prefix, publisher, idt);
    }
}
