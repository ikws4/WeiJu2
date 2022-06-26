package io.ikws4.codeeditor.api.configuration;

import androidx.annotation.ColorInt;

public interface ColorScheme {
    @ColorInt
    int getBackgroundColor();

    @ColorInt
    int getTextColor();

    @ColorInt
    int getGutterColor();

    @ColorInt
    int getGutterDividerColor();

    @ColorInt
    int getGutterTextColor();

    @ColorInt
    int getGutterActiveTextColor();

    @ColorInt
    int getCursorLineColor();

    @ColorInt
    int getSelectionColor();

    @ColorInt
    int getCompletionMenuBackgroundColor();

    @ColorInt
    int getIndentColor();

    SyntaxColorScheme getSyntaxColorScheme();
}

