package io.ikws4.codeeditor;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This provide the same features like {@link CodeEditor}, but read-only.
 */
public class CodeViewer extends CodeEditor {
    public CodeViewer(@NonNull Context context) {
        super(context);
    }

    public CodeViewer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CodeViewer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isViwer() {
        return true;
    }
}
