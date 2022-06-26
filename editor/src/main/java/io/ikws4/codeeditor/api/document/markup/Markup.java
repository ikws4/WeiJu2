package io.ikws4.codeeditor.api.document.markup;

public interface Markup {
    int getStart();

    int getEnd();

    void shift(int offset);
}
