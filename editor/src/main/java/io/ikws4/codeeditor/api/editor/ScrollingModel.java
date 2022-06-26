package io.ikws4.codeeditor.api.editor;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import io.ikws4.codeeditor.api.editor.listener.VisibleAreaListener;

public interface ScrollingModel {
    void scrollToCaret();

    void scrollTo(int x, int y);

    void scrollBy(int x, int y);

    @NonNull
    Rect getVisibleArea();

    int getScrollX();

    int getScrollY();

    void addVisibleAreaListener(@NonNull VisibleAreaListener l);

    void removeVisibleAreaListener(@NonNull VisibleAreaListener l);
}
