package io.ikws4.codeeditor.api.editor.component;

import io.ikws4.codeeditor.api.editor.Editor;

public interface Component {
    void onAttachEditor(Editor editor);

    int getComponentWidth();

    int getComponentHeight();
}
