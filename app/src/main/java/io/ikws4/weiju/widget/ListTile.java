package io.ikws4.weiju.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.imageview.ShapeableImageView;

import io.ikws4.weiju.R;
import io.ikws4.weiju.util.UnitConverter;

public class ListTile extends CustomView {

    public ListTile(@NonNull Context context) {
        super(context);
    }

    public ListTile(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ListTile, 0, 0);
        ShapeableImageView vIcon = findViewById(R.id.v_icon);
        int iconResourceId = arr.getResourceId(R.styleable.ListTile_icon, 0);
        if (iconResourceId == 0) {
            vIcon.setVisibility(GONE);
        } else {
            vIcon.setImageResource(iconResourceId);
        }

        TextView vTitle = findViewById(R.id.v_title);
        vTitle.setText(arr.getText(R.styleable.ListTile_title));

        TextView vSubtitle = findViewById(R.id.v_subtitle);
        vSubtitle.setText(arr.getText(R.styleable.ListTile_subtitle));

        if (TextUtils.isEmpty(vTitle.getText())) {
            vTitle.setVisibility(GONE);
        }

        if (TextUtils.isEmpty(vSubtitle.getText())) {
            vSubtitle.setVisibility(GONE);
        }

        int iconGravity = arr.getInteger(R.styleable.ListTile_icon_gravity, Gravity.CENTER);
        if (iconGravity == Gravity.TOP) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) vIcon.getLayoutParams();
            params.bottomToBottom = ConstraintSet.UNSET;
            vIcon.setLayoutParams(params);
        }

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) vTitle.getLayoutParams();
        if (vSubtitle.getVisibility() == GONE) {
            params.bottomToBottom = ConstraintSet.PARENT_ID;
        }
        if (iconResourceId == 0) {
            params.leftMargin += UnitConverter.dp(40);
        } else {
            params.leftMargin += UnitConverter.dp(8);
        }
        vTitle.setLayoutParams(params);

        arr.recycle();
    }

    @Override
    public int inflateId() {
        return R.layout.list_tile;
    }
}
