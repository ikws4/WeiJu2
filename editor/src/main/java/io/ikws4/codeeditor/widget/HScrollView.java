package io.ikws4.codeeditor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class HScrollView extends HorizontalScrollView {
    public HScrollView(Context context) {
        this(context, null);
    }

    public HScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHorizontalFadingEdgeEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setFillViewport(true);
    }
}
