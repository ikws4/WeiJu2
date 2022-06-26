package io.ikws4.codeeditor.api.configuration;

import androidx.annotation.ColorInt;

public interface SyntaxColorScheme {
    @ColorInt
    int getAnnotationColor();

    @ColorInt
    int getConstantColor();

    @ColorInt
    int getCommentColor();

    @ColorInt
    int getNumberColor();

    @ColorInt
    int getOperatorColor();

    @ColorInt
    int getKeywordColor();

    @ColorInt
    int getTypeColor();

    @ColorInt
    int getMethodColor();

    @ColorInt
    int getStringColor();
}
