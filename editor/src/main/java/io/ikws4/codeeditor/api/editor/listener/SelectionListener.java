package io.ikws4.codeeditor.api.editor.listener;

public interface SelectionListener {
    void onSelectionChanged(int start, int end, int oldStart, int oldEnd);
}
