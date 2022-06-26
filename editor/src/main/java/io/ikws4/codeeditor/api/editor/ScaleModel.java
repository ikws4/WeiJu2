package io.ikws4.codeeditor.api.editor;

import androidx.annotation.NonNull;

import io.ikws4.codeeditor.api.editor.listener.ScaleListener;

public interface ScaleModel {
    float getScaleFactor();

    void addScaleListener(@NonNull ScaleListener l);

    void removeScaleListener(@NonNull ScaleListener l);
}