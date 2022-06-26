package io.ikws4.codeeditor.api.editor;

import androidx.annotation.NonNull;

import io.ikws4.codeeditor.api.editor.listener.SelectionListener;

public interface SelectionModel {
    int getSelectionStart();

    int getSelectionEnd();

    @NonNull
    CharSequence getSelectionText();

    boolean hasSelection();

    void setSelection(int startOffset, int endOffset);

    void removeSelection();

    void addSelectionListener(@NonNull SelectionListener l);

    void removeSelectionListener(@NonNull SelectionListener l);

    void selectionLineAtCaret();

    void moveUp();

    void moveDown();

    void moveLeft();

    void moveRight();
}
