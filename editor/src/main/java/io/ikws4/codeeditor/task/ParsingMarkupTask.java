package io.ikws4.codeeditor.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import io.ikws4.codeeditor.api.editor.Editor;
import io.ikws4.codeeditor.api.configuration.SyntaxColorScheme;
import io.ikws4.codeeditor.api.document.markup.Markup;
import io.ikws4.codeeditor.api.language.LanguageStyler;

public class ParsingMarkupTask extends AsyncTask<Void, Void, List<Markup>> {
    private final WeakReference<Editor> mEditor;
    private final TaskFinishedListener<List<Markup>> mListener;

    public ParsingMarkupTask(Editor editor, TaskFinishedListener<List<Markup>> listener) {
        super();
        mEditor = new WeakReference<>(editor);
        mListener = listener;
    }

    @Override
    protected List<Markup> doInBackground(Void... voids) {
        Editor editor = mEditor.get();
        String text = editor.getDocument().toString();
        LanguageStyler highlighter = editor.getLanguage().getStyler();
        SyntaxColorScheme syntaxScheme = editor.getConfiguration().getColorScheme().getSyntaxColorScheme();
        return highlighter.process(text, syntaxScheme);
    }

    @Override
    protected void onPostExecute(List<Markup> spans) {
        mListener.onFinished(spans);
    }
}
