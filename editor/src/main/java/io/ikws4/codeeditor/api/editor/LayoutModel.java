package io.ikws4.codeeditor.api.editor;

public interface LayoutModel {
    /**
     * Return the vertical position of the baseline of the specified line.
     */
    int getLineBaseline(int line);

    /**
     * Return the current line of the layout.
     */
    int getCurrentLine();

    /**
     * Return the top visible line of the layout.
     */
    int getTopLine();

    /**
     * Return the bottom visible line of the layout.
     */
    int getBottomLine();

    /**
     * Return the number of lines of text in this layout.
     */
    int getLineCount();
}
