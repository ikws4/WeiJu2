package io.ikws4.codeeditor.task;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import io.ikws4.codeeditor.api.editor.Editor;

public class FormatTask extends AsyncTask<Void, Void, String> {
    private final WeakReference<Editor> mEditor;
    private final TaskFinishedListener<String> mListener;

    public FormatTask(Editor editor, TaskFinishedListener<String> listener) {
        super();
        mEditor = new WeakReference<>(editor);
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        Editor editor = mEditor.get();
        String text = editor.getDocument().toString();
        return editor.getLanguage().getStyler().format(text);
    }

    @Override
    protected void onPostExecute(String s) {
        mListener.onFinished(s);
    }
}
