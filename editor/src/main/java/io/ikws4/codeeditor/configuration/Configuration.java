package io.ikws4.codeeditor.configuration;


import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.configuration.colorscheme.ColorSchemes;
import io.ikws4.codeeditor.configuration.indent.Indentation;

public class Configuration {
    public ColorScheme colorScheme = ColorSchemes.ROSE_PINE;

    public float fontSize = 14.0f;

    /**
     * When on, lines longer than the width of the window will wrap and
     * displaying continues on the next line.
     */
    public boolean wrap = false;

    /**
     * Complete the part of keywords or line that has been typed.
     * This is useful if your are using complicated keywords.
     */
    public boolean completion = true;

    //TODO: pinchZoom doc
    public boolean pinchZoom = true;

    /**
     * Draw the line number in front of each line.
     */
    public boolean number = false;

    // TODO: cursorLine doc with hl-CursorLine
    /**
     * When on, Highlight the screen line of the cursor.
     */
    public boolean cursorLine = false;

    // TODO: highlightDelimiter doc
    public boolean highlightDelimiter = true;

    /**
     * Copy indent from current line when starting a new line.
     */
    public boolean autoIndent = true;

    public Indentation indentation = Indentation.WHITE_SPACE_2;
}
