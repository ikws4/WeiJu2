package io.ikws4.weiju.editor;

import android.graphics.Color;

import org.eclipse.tm4e.core.grammar.IGrammar;
import org.eclipse.tm4e.core.grammar.ITokenizeLineResult2;
import org.eclipse.tm4e.core.internal.grammar.StackElementMetadata;
import org.eclipse.tm4e.core.internal.grammar.StandardTokenType;
import org.eclipse.tm4e.core.internal.oniguruma.OnigRegExp;
import org.eclipse.tm4e.core.internal.oniguruma.OnigResult;
import org.eclipse.tm4e.core.internal.oniguruma.OnigString;
import org.eclipse.tm4e.core.registry.Registry;
import org.eclipse.tm4e.core.theme.FontStyle;
import org.eclipse.tm4e.core.theme.IRawTheme;
import org.eclipse.tm4e.core.theme.Theme;
import org.eclipse.tm4e.languageconfiguration.ILanguageConfiguration;
import org.eclipse.tm4e.languageconfiguration.internal.LanguageConfigurator;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.github.rosemoe.sora.lang.analysis.AsyncIncrementalAnalyzeManager;
import io.github.rosemoe.sora.lang.brackets.BracketsProvider;
import io.github.rosemoe.sora.lang.brackets.OnlineBracketsMatcher;
import io.github.rosemoe.sora.lang.completion.IdentifierAutoComplete;
import io.github.rosemoe.sora.lang.styling.CodeBlock;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.TextStyle;
import io.github.rosemoe.sora.langs.textmate.MyState;
import io.github.rosemoe.sora.langs.textmate.folding.FoldingHelper;
import io.github.rosemoe.sora.langs.textmate.folding.IndentRange;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.util.ArrayList;
import io.github.rosemoe.sora.util.MyCharacter;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

class TextMateAnalyzer extends AsyncIncrementalAnalyzeManager<MyState, Span> implements FoldingHelper {

    private final Registry registry = new Registry();
    private final IGrammar grammar;
    private Theme theme;
    private final TextMateLanguage language;
    private final ILanguageConfiguration configuration;
    private OnigRegExp cachedRegExp;
    private boolean foldingOffside;
    private BracketsProvider bracketsProvider;
    final IdentifierAutoComplete.SyncIdentifiers syncIdentifiers = new IdentifierAutoComplete.SyncIdentifiers();

    public TextMateAnalyzer(TextMateLanguage language, String grammarName, InputStream grammarIns, Reader languageConfiguration, IRawTheme theme) throws Exception {
        registry.setTheme(theme);
        this.language = language;
        this.theme = Theme.createFromRawTheme(theme);
        this.grammar = registry.loadGrammarFromPathSync(grammarName, grammarIns);
        if (languageConfiguration != null) {
            LanguageConfigurator languageConfigurator = new LanguageConfigurator(languageConfiguration);
            configuration = languageConfigurator.getLanguageConfiguration();
            var pairs = configuration.getBrackets();
            if (pairs != null && pairs.size() != 0) {
                var pairArr = new char[pairs.size() * 2];
                int i = 0;
                for (var pair : pairs) {
                    pairArr[i * 2] = pair.getKey().charAt(0);
                    pairArr[i * 2 + 1] = pair.getValue().charAt(0);
                    i++;
                }
                bracketsProvider = new OnlineBracketsMatcher(pairArr, 100000);
            }
        } else {
            configuration = null;
        }
        createFoldingExp();
    }

    private void createFoldingExp() {
        if (configuration == null) {
            return;
        }
        var markers = configuration.getFolding();
        if (markers == null) return;
        foldingOffside = markers.getOffSide();
        cachedRegExp = new OnigRegExp("(" + markers.getMarkersStart() + ")|(?:" + markers.getMarkersEnd() + ")");
    }

    @Override
    public MyState getInitialState() {
        return null;
    }

    @Override
    public boolean stateEquals(MyState state, MyState another) {
        if (state == null && another == null) {
            return true;
        }
        if (state != null && another != null) {
            return Objects.equals(state.tokenizeState, another.tokenizeState);
        }
        return false;
    }

    @Override
    public int getIndentFor(int line) {
        return getState(line).state.indent;
    }

    @Override
    public OnigResult getResultFor(int line) {
        return getState(line).state.foldingCache;
    }

    @Override
    public List<CodeBlock> computeBlocks(Content text, CodeBlockAnalyzeDelegate delegate) {
        var list = new ArrayList<CodeBlock>();
        analyzeCodeBlocks(text, list, delegate);
        if (delegate.isNotCancelled()) {
            var r = getReceiver();
            if (r != null) {
                r.updateBracketProvider(this, bracketsProvider);
            }
        }
        return list;
    }

    public void analyzeCodeBlocks(Content model, ArrayList<CodeBlock> blocks, CodeBlockAnalyzeDelegate delegate) {
        if (cachedRegExp == null) {
            return;
        }
        try {
            var foldingRegions = IndentRange.computeRanges(model, language.getTabSize(), foldingOffside, this, cachedRegExp, delegate);
            blocks.ensureCapacity(foldingRegions.length());
            for (int i = 0; i < foldingRegions.length() && delegate.isNotCancelled(); i++) {
                int startLine = foldingRegions.getStartLineNumber(i);
                int endLine = foldingRegions.getEndLineNumber(i);
                if (startLine != endLine) {
                    CodeBlock codeBlock = new CodeBlock();
                    codeBlock.toBottomOfEndLine = true;
                    codeBlock.startLine = startLine;
                    codeBlock.endLine = endLine;

                    // It's safe here to use raw data because the Content is only held by this thread
                    var length = model.getColumnCount(startLine);
                    var chars = model.getLine(startLine).getRawData();

                    codeBlock.startColumn = IndentRange.computeStartColumn(chars, length, language.getTabSize());
                    codeBlock.endColumn = codeBlock.startColumn;
                    blocks.add(codeBlock);
                }
            }
            Collections.sort(blocks, CodeBlock.COMPARATOR_END);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized LineTokenizeResult<MyState, Span> tokenizeLine(CharSequence lineC, MyState state, int lineIndex) {
        String line = (lineC instanceof ContentLine) ? ((ContentLine) lineC).toStringWithNewline() : lineC.toString();
        var tokens = new ArrayList<Span>();
        ITokenizeLineResult2 lineTokens = grammar.tokenizeLine2(line, state == null ? null : state.tokenizeState);
        int tokensLength = lineTokens.getTokens().length / 2;
        var identifiers = language.createIdentifiers ? new ArrayList<String>() : null;
        for (int i = 0; i < tokensLength; i++) {
            int startIndex = lineTokens.getTokens()[2 * i];
            if (i == 0 && startIndex != 0) {
                tokens.add(Span.obtain(0, EditorColorScheme.TEXT_NORMAL));
            }
            int metadata = lineTokens.getTokens()[2 * i + 1];
            int foreground = StackElementMetadata.getForeground(metadata);
            int fontStyle = StackElementMetadata.getFontStyle(metadata);
            if (language.createIdentifiers) {
                var type = StackElementMetadata.getTokenType(metadata);

                if (type == StandardTokenType.Other) {
                    var end = i + 1 == tokensLength ? lineC.length() : lineTokens.getTokens()[2 * (i + 1)];

                    int _startIndex = startIndex;
                    int _end = end - 1;
                    while (_startIndex <= _end && line.charAt(_end) == ' ') {
                        _end--;
                    }
                    while (_startIndex <= _end && line.charAt(_startIndex) == ' ') {
                        _startIndex++;
                    }

                    if (_startIndex <= _end && MyCharacter.isJavaIdentifierStart(line.charAt(_startIndex))) {
                        var flag = true;
                        for (int j = _startIndex + 1; j <= _end; j++) {
                            if (!MyCharacter.isJavaIdentifierPart(line.charAt(j))) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag && !isKeywords(line, _startIndex, _end)) {
                            identifiers.add(line.substring(_startIndex, _end + 1));
                        }
                    }
                }
            }
            Span span = Span.obtain(startIndex, TextStyle.makeStyle(foreground + 255, 0, (fontStyle & FontStyle.Bold) != 0, (fontStyle & FontStyle.Italic) != 0, false));

            if ((fontStyle & FontStyle.Underline) != 0) {
                String color = theme.getColor(foreground);
                if (color != null) {
                    span.underlineColor = Color.parseColor(color);
                }
            }

            tokens.add(span);
        }
        return new LineTokenizeResult<>(new MyState(lineTokens.getRuleStack(), cachedRegExp == null ? null : cachedRegExp.search(new OnigString(line), 0), IndentRange.computeIndentLevel(((ContentLine) lineC).getRawData(), line.length() - 1, language.getTabSize()), identifiers), null, tokens);
    }

    private boolean isKeywords(String line, int l, int r) {
        String[] keywords = language.autoComplete.getKeywords();
        int len = r - l + 1;
        for (String keyword : keywords) {
            if (len == keyword.length()) {
                boolean flag = true;
                for (int j = 0; j < len; j++) {
                    if (line.charAt(l + j) != keyword.charAt(j)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) return true;
            }
        }
        return false;
    }

    @Override
    public void onAddState(MyState state) {
        super.onAddState(state);
        if (language.createIdentifiers) {
            for (String identifier : state.identifiers) {
                syncIdentifiers.identifierIncrease(identifier);
            }
        }
    }

    @Override
    public void onAbandonState(MyState state) {
        super.onAbandonState(state);
        if (language.createIdentifiers) {
            for (String identifier : state.identifiers) {
                syncIdentifiers.identifierDecrease(identifier);
            }
        }
    }

    @Override
    public List<Span> generateSpansForLine(LineTokenizeResult<MyState, Span> tokens) {
        return null;
    }

    public void updateTheme(IRawTheme theme) {
        registry.setTheme(theme);
        this.theme = Theme.createFromRawTheme(theme);
    }

}
