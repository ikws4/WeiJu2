package io.ikws4.weiju.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;

import io.ikws4.weiju.R;

public class ViewFlipper extends android.widget.ViewFlipper {
    public ViewFlipper(Context context) {
        super(context);
        init();
    }

    public ViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
    }

    @Override
    public void setDisplayedChild(int whichChild) {
        if (getDisplayedChild() != whichChild) {
            super.setDisplayedChild(whichChild);
        }
    }
}
