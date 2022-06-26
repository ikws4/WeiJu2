package io.ikws4.codeeditor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class VScrollView extends ScrollView {
    public VScrollView(Context context) {
        this(context, null);
    }

    public VScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalFadingEdgeEnabled(false);
        setVerticalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setFillViewport(true);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
    }
}
