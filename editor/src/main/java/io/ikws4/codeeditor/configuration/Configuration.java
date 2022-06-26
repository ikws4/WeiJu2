package io.ikws4.codeeditor.configuration;


import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.configuration.colorscheme.ColorSchemes;
import io.ikws4.codeeditor.configuration.indent.Indentation;

public class Configuration {
    private ColorScheme colorScheme = ColorSchemes.ROSE_PINE;

    private float fontSize = 14.0f;

    /**
     * When on, lines longer than the width of the window will wrap and
     * displaying continues on the next line.
     */
    private boolean wrap = false;

    /**
     * Complete the part of keywords or line that has been typed.
     * This is useful if your are using complicated keywords.
     */
    private boolean completion = true;

    //TODO: pinchZoom doc
    private boolean pinchZoom = true;

    /**
     * Draw the line number in front of each line.
     */
    private boolean number = false;

    // TODO: cursorLine doc with hl-CursorLine
    /**
     * When on, Highlight the screen line of the cursor.
     */
    private boolean cursorLine = false;

    // TODO: highlightDelimiter doc
    private boolean highlightDelimiter = true;

    /**
     * Copy indent from current line when starting a new line.
     */
    private boolean autoIndent = true;

    private Indentation indentation = Indentation.WHITE_SPACE_2;

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public boolean isCompletion() {
        return completion;
    }

    public void setCompletion(boolean completion) {
        this.completion = completion;
    }

    public boolean isPinchZoom() {
        return pinchZoom;
    }

    public void setPinchZoom(boolean pinchZoom) {
        this.pinchZoom = pinchZoom;
    }

    public boolean isNumber() {
        return number;
    }

    public void setNumber(boolean number) {
        this.number = number;
    }

    public boolean isCursorLine() {
        return cursorLine;
    }

    public void setCursorLine(boolean cursorLine) {
        this.cursorLine = cursorLine;
    }

    public boolean isHighlightDelimiter() {
        return highlightDelimiter;
    }

    public void setHighlightDelimiter(boolean highlightDelimiter) {
        this.highlightDelimiter = highlightDelimiter;
    }

    public boolean isAutoIndent() {
        return autoIndent;
    }

    public void setAutoIndent(boolean autoIndent) {
        this.autoIndent = autoIndent;
    }

    public Indentation getIndentation() {
        return indentation;
    }

    public void setIndentation(Indentation indentation) {
        this.indentation = indentation;
    }
}
