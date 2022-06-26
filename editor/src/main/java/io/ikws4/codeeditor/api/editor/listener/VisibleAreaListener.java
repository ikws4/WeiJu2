package io.ikws4.codeeditor.api.editor.listener;

import android.graphics.Rect;

public interface VisibleAreaListener {
    void onVisibleAreaChanged(Rect rect, Rect oldRect);
}
