package io.ikws4.weiju.editor;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import org.eclipse.tm4e.core.theme.IRawTheme;
import org.eclipse.tm4e.languageconfiguration.internal.LanguageConfiguration;
import org.eclipse.tm4e.languageconfiguration.internal.supports.IndentationRule;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.weiju.util.Strings;

class LuaLanguage extends EmptyLanguage {
    private TextMateLanguage mTextMateLanguage;
    private IndentationRule mIndentationRule;
    private final Editor mEditor;

    public LuaLanguage(Editor editor, IRawTheme theme) {
        mEditor = editor;

        try {
            AssetManager assets = editor.getContext().getAssets();
            String configuration = "textmate/lang/lua/language-configuration.json";
            String grammar = "textmate/lang/lua/tmLanguage.json";

            mTextMateLanguage = TextMateLanguage.create(grammar, assets.open(grammar), new InputStreamReader(assets.open(configuration)), theme);
            mIndentationRule = LanguageConfiguration.load(new InputStreamReader(assets.open(configuration))).getIndentationRule();

            String[] keywords = {
                "and", "break", "do", "else", "elseif",
                "end", "false", "for", "function", "if",
                "in", "local", "nil", "not", "or",
                "repeat", "return", "then", "true", "until", "while"
            };
            mTextMateLanguage.setKeywords(keywords, true);
            DiagnosticTask.launch(editor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getIndentAdvance(@NonNull ContentReference content, int line, int column) {
        return getIndentAdvance(content.getLine(line).substring(0, column));
    }

    public int getIndentAdvance(String line) {
        if (line.matches(mIndentationRule.getIncreaseIndentPattern())) {
            return getTabSize();
        }
        return 0;
    }

    public void updateTheme(IRawTheme theme) {
        mTextMateLanguage.updateTheme(theme);
    }

    public void setTabSize(int tabSize) {
        mTextMateLanguage.setTabSize(tabSize);
    }

    public int getTabSize() {
        return mTextMateLanguage.getTabSize();
    }

    public boolean isAutoCompleteEnabled() {
        return mTextMateLanguage.isAutoCompleteEnabled();
    }

    public void setAutoCompleteEnabled(boolean autoCompleteEnabled) {
        mTextMateLanguage.setAutoCompleteEnabled(autoCompleteEnabled);
    }

    private final NewlineHandler[] mNewlineHandlers = new NewlineHandler[]{new EndwiseNewlineHandler()};

    @Override
    public NewlineHandler[] getNewlineHandlers() {
        return mNewlineHandlers;
    }

    @Override
    public SymbolPairMatch getSymbolPairs() {
        return mTextMateLanguage.getSymbolPairs();
    }

    @Override
    @NonNull
    public AnalyzeManager getAnalyzeManager() {
        return mTextMateLanguage.getAnalyzeManager();
    }

    @Override
    public void requireAutoComplete(@NonNull ContentReference content, @NonNull CharPosition position, @NonNull CompletionPublisher publisher, @NonNull Bundle extraArguments) {
        mTextMateLanguage.requireAutoComplete(content, position, publisher, extraArguments);
    }

    class EndwiseNewlineHandler implements NewlineHandler {
        private static final String ENDWISE_PATTERN = "^((?!(--)).)*(\\b(else|function|then|do|repeat)\\b((?!\\b(end|until)\\b).)*)$";

        @Override
        public boolean matchesRequirement(String beforeText, String afterText) {
            return beforeText.matches(mIndentationRule.getIncreaseIndentPattern());
        }

        private final StringBuilder mStringBuilder = new StringBuilder();

        @Override
        public NewlineHandleResult handleNewline(String beforeText, String afterText, int tabSize) {
            String leadingSpaces = Strings.repeat(" ", Strings.leadingSpaceCount(beforeText));
            int indent = getIndentAdvance(beforeText);

            boolean shouldAddEnd = false;
            int nextLine = mEditor.getCursor().getLeftLine() + 1;

            int lines = mEditor.getText().getLineCount();
            if (nextLine <= lines) {
                if (nextLine == mEditor.getText().getLineCount()) {
                    shouldAddEnd = true;
                } else {
                    ContentLine line;
                    do {
                        line = mEditor.getText().getLine(nextLine++);
                    } while (nextLine < lines && Strings.isOnlySpaces(line));

                    if (!line.toString().startsWith(leadingSpaces + "end")) {
                        shouldAddEnd = true;
                    }
                }
            }
            mStringBuilder.setLength(0);
            mStringBuilder.append("\n");
            mStringBuilder.append(Strings.repeat(" ", indent + leadingSpaces.length()));
            int leftShift = 0;
            if (shouldAddEnd) {
                mStringBuilder.append("\n");

                if (beforeText.matches(ENDWISE_PATTERN)) {
                    mStringBuilder.append(leadingSpaces).append("end");
                    leftShift = leadingSpaces.length() + 4;
                } else {
                    leftShift = leadingSpaces.length() + 1;
                }
            }

            return new NewlineHandleResult(mStringBuilder.toString(), leftShift);
        }
    }

    static class DiagnosticTask extends AsyncTask<Content, Void, DiagnosticsContainer> {
        private final WeakReference<Editor> mEditor;

        private DiagnosticTask(Editor editor) {
            mEditor = new WeakReference<>(editor);
        }

        public static void launch(Editor editor) {
            new DiagnosticTask(editor).execute(editor.getText());
        }

        @Override
        protected DiagnosticsContainer doInBackground(Content... references) {
            InputStream in = new ByteArrayInputStream(references[0].toString().getBytes());
            LuaParser parser = new LuaParser(new BufferedInputStream(in));
            DiagnosticsContainer diagnosticsContainer = new DiagnosticsContainer();
            try {
                Chunk chunk = parser.Chunk();
                chunk.accept(new Visitor() {
                });
            } catch (ParseException e) {
                var token = e.currentToken;
                if (token != null) {
                    var content = mEditor.get().getText();
                    int startIndex = content.getCharIndex(token.beginLine - 1, token.beginColumn - 1);
                    int endIndex = content.getCharIndex(token.endLine - 1, token.endColumn - 1);
                    diagnosticsContainer.addDiagnostic(new DiagnosticRegion(startIndex, endIndex, DiagnosticRegion.SEVERITY_ERROR));
                }
            }
            return diagnosticsContainer;
        }

        @Override
        protected void onPostExecute(DiagnosticsContainer diagnostics) {
            super.onPostExecute(diagnostics);
            Editor editor = mEditor.get();
            editor.setDiagnostics(diagnostics);
            editor.postDelayed(() -> DiagnosticTask.launch(editor), 1000);
        }

        private DiagnosticRegion newErrorDiagnosticResion(Stat stat) {
            return newDiagnosticResion(stat, DiagnosticRegion.SEVERITY_ERROR);
        }

        private DiagnosticRegion newDiagnosticResion(Stat stat, short severity) {
            var content = mEditor.get().getText();
            int startIndex = content.getCharIndex(stat.beginLine - 1, stat.beginColumn - 1);
            int endIndex = content.getCharIndex(stat.endLine - 1, stat.endColumn - 1);
            return new DiagnosticRegion(startIndex, endIndex, severity);
        }
    }
}
