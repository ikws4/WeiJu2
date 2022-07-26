package io.ikws4.weiju.widget;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import io.ikws4.weiju.R;

public class Dialog extends android.app.Dialog {
    public Dialog(@NonNull Context context, @LayoutRes int layoutRes) {
        super(context, R.style.Dialog_WeiJu);
        setContentView(layoutRes);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // bluring
        getWindow().setDimAmount(0.3f);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.85);
        getWindow().setAttributes(lp);
    }
}
