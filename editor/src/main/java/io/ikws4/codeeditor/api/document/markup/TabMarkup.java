package io.ikws4.codeeditor.api.document.markup;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;

import androidx.annotation.ColorInt;

public class TabMarkup extends ReplacedMarkup implements LeadingMarginSpan {
    private final String mTab;
    private final int mColor;
    private float[] mPoints;

    public TabMarkup(String tab, @ColorInt int color, int start, int end) {
        super(new char[0], start, end);
        mTab = tab;
        mColor = color;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        if (mTab.length() <= 0) return;
        if (mPoints == null) {
            float charWidth = p.measureText(String.valueOf(mTab.charAt(0)));
            int centerY = top + (bottom - top) / 2;
            mPoints = new float[2 * mTab.length()];
            for (int i = 0; i < mPoints.length; i += 2) {
                mPoints[i] = charWidth * (i + 1) / 2;
                mPoints[i + 1] = centerY;
            }

        }
        TextPaint paint = new TextPaint(p);
        paint.setColor(mColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStrokeCap(Paint.Cap.ROUND);
        c.drawPoints(mPoints, paint);
    }
}
