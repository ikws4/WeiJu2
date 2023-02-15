package io.ikws4.weiju.editor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.eclipse.tm4e.languageconfiguration.model.IndentationRules;

import java.lang.ref.WeakReference;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.completion.CompletionPublisher;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion;
import io.github.rosemoe.sora.lang.diagnostic.DiagnosticsContainer;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult;
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.TextMateSymbolPairMatch;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentLine;
import io.github.rosemoe.sora.text.ContentReference;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.weiju.lua.DiagnosticInfo;
import io.ikws4.weiju.lua.LuaDiagnostic;
import io.ikws4.weiju.util.Strings;

class LuaLanguage extends EmptyLanguage {
    private final TextMateLanguage mTextMateLanguage;
    private final IndentationRules mIndentationRules;
    private final Editor mEditor;

    public LuaLanguage(Editor editor) {
        mEditor = editor;
        mTextMateLanguage = TextMateLanguage.create("source.lua", true);
        mIndentationRules = GrammarRegistry.getInstance().findLanguageConfiguration("source.lua").getIndentationRules();

        String[] keywords = {
            "and", "break", "do", "else", "elseif",
            "end", "false", "for", "function", "if",
            "in", "local", "nil", "not", "or",
            "repeat", "return", "then", "true", "until", "while"
        };
        mTextMateLanguage.setCompleterKeywords(keywords);
        mTextMateLanguage.setTabSize(editor.getTabWidth());
        ((TextMateSymbolPairMatch)mTextMateLanguage.getSymbolPairs()).setEnabled(true);

        DiagnosticTask.launch(editor);
    }

    @Override
    public int getIndentAdvance(@NonNull ContentReference content, int line, int column) {
        return getIndentAdvance(content.getLine(line).substring(0, column));
    }

    public int getIndentAdvance(String line) {
        return line.matches(mIndentationRules.increaseIndentPattern.pattern()) ? mEditor.getTabWidth() : 0;
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

    public class EndwiseNewlineHandler implements NewlineHandler {
        private static final String ENDWISE_PATTERN = "^((?!(--)).)*(\\b(else|function|then|do|repeat)\\b((?!\\b(end|until)\\b).)*)$";

        private final StringBuilder mStringBuilder = new StringBuilder();

        @Override
        public boolean matchesRequirement(@NonNull Content text, @NonNull CharPosition position, @Nullable Styles style) {
            String line = text.getLineString(position.line);
            String beforeText = line.substring(0, position.column);

            return beforeText.matches(ENDWISE_PATTERN);
        }

        @NonNull
        @Override
        public NewlineHandleResult handleNewline(@NonNull Content text, @NonNull CharPosition position, @Nullable Styles style, int tabSize) {
            String line = text.getLineString(position.line);
            String beforeText = line.substring(0, position.column);
            String afterText = line.substring(position.column);

            String leadingSpaces = Strings.repeat(" ", Strings.leadingSpaceCount(beforeText));
            String indent = Strings.repeat(" ", getIndentAdvance(beforeText));

            if (beforeText.endsWith("{") && !afterText.startsWith("}")) return new NewlineHandleResult("\n" + leadingSpaces + indent, 0);

            boolean shouldAddEnd = false;
            int nextLine = mEditor.getCursor().getLeftLine() + 1;

            int lines = mEditor.getText().getLineCount();
            if (nextLine <= lines) {
                if (nextLine == mEditor.getText().getLineCount()) {
                    shouldAddEnd = true;
                } else {
                    ContentLine contentLine;
                    do {
                        contentLine = mEditor.getText().getLine(nextLine++);
                    } while (nextLine < lines && Strings.isOnlySpaces(contentLine));

                    if (!contentLine.toString().startsWith(leadingSpaces + "end")) {
                        shouldAddEnd = true;
                    }
                }
            }
            mStringBuilder.setLength(0);
            mStringBuilder.append("\n");
            mStringBuilder.append(leadingSpaces).append(indent);
            int leftShift = 0;
            if (shouldAddEnd) {
                mStringBuilder.append("\n");

                mStringBuilder.append(leadingSpaces);
                leftShift += leadingSpaces.length() + 1;
                if (beforeText.matches(ENDWISE_PATTERN)) {
                    mStringBuilder.append("end");
                    leftShift += 3;
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
            DiagnosticTask task = new DiagnosticTask(editor);
            task.execute(editor.getText());

            editor.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    task.cancel(true);
                }
            });
        }

        @Override
        protected DiagnosticsContainer doInBackground(Content... references) {
            DiagnosticsContainer diagnosticsContainer = new DiagnosticsContainer();
            LuaDiagnostic.diagnose(references[0].toString(), info -> {
                try {
                    diagnosticsContainer.addDiagnostic(newDiagnosticResion(info, DiagnosticRegion.SEVERITY_ERROR));
                } catch (StringIndexOutOfBoundsException ignored) {
                }
            });
            return diagnosticsContainer;
        }

        @Override
        protected void onPostExecute(DiagnosticsContainer diagnostics) {
            super.onPostExecute(diagnostics);
            Editor editor = mEditor.get();
            editor.setDiagnostics(diagnostics);
            editor.postDelayed(() -> DiagnosticTask.launch(editor), 1000);
        }

        private DiagnosticRegion newDiagnosticResion(DiagnosticInfo info, short severity) {
            Content content = mEditor.get().getText();
            int startIndex = content.getCharIndex(info.startLine, info.startColumn);
            int endIndex = content.getCharIndex(info.endLine, info.endColumn) + 1;
            info.recycle();
            return new DiagnosticRegion(startIndex, endIndex, severity);
        }
    }
}
