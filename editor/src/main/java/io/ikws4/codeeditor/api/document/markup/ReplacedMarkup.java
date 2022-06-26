package io.ikws4.codeeditor.api.document.markup;

import android.text.NoCopySpan;
import android.view.KeyEvent;

import io.ikws4.codeeditor.component.TextArea;

public class ReplacedMarkup implements NoCopySpan, Markup {
    private final char[] mText;
    private int mStart;
    private int mEnd;

    public ReplacedMarkup(char[] text, int start, int end) {
        mText = text;
        mStart = start;
        mEnd = end;
    }

    public char[] getText() {
        return mText;
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
}
