package io.ikws4.weiju.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class CustomView extends FrameLayout {
    public CustomView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        inflate(getContext(), inflateId(), this);
    }

    public abstract int inflateId();
}
