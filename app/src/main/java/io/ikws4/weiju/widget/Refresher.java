package io.ikws4.weiju.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.ikws4.weiju.R;

public class Refresher extends SwipeRefreshLayout {

    public Refresher(@NonNull Context context) {
        super(context);
        init();
    }

    public Refresher(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int indicatorColor = getResources().getColor(R.color.iris);
        int backgroundColor = getResources().getColor(R.color.surface);
        setColorSchemeColors(indicatorColor);
        setProgressBackgroundColorSchemeColor(backgroundColor);
    }
}
