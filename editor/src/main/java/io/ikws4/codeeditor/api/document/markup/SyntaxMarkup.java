package io.ikws4.codeeditor.api.document.markup;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.CharacterStyle;

import androidx.annotation.ColorInt;

/**
 * A markup that support {@link Typeface} and foreground color
 */
public class SyntaxMarkup extends CharacterStyle implements Markup {
    private final int mStyle;
    private final int mColor;
    private int mStart;
    private int mEnd;

    public SyntaxMarkup(@ColorInt int color, int start, int end) {
        this(Typeface.NORMAL, color, start, end);
    }

    /**
     * @param style An integer constant describing the style for this span. Examples
     *              include bold, italic, and normal. Values are constants defined
     *              in {@link Typeface}.
     * @param color text color
     */
    public SyntaxMarkup(int style, @ColorInt int color, int start, int end) {
        mStyle = style;
        mColor = color;
        mStart = start;
        mEnd = end;
    }

    @Override
    public int getStart() {
        return mStart;
    }

    @Override
    public int getEnd() {
        return mEnd;
    }

    @Override
    public void shift(int offset) {
        mStart += offset;
        mEnd += offset;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        apply(paint, mStyle, mColor);
    }

    @SuppressLint("WrongConstant")
    private static void apply(TextPaint paint, int style, int color) {

        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int want = oldStyle | style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
        paint.setColor(color);
    }
}