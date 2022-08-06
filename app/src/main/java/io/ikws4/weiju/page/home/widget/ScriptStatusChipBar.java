package io.ikws4.weiju.page.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.ikws4.weiju.R;

public class ScriptStatusChipBar extends FrameLayout {
    private TextView vNewVersion, vPackage;

    public ScriptStatusChipBar(@NonNull Context context) {
        super(context);
        init();
    }

    public ScriptStatusChipBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.script_status_chip_bar, this, true);
        vNewVersion = findViewById(R.id.chip_new_version);
        vPackage = findViewById(R.id.chip_package);
    }

    public void setScriptStatus(ScriptListView.ScriptItem item) {
        toggleVisibility(this, item.hasNewVersion || item.isPackage);
        toggleVisibility(vNewVersion, item.hasNewVersion);
        toggleVisibility(vPackage, item.isPackage);
    }

    private void toggleVisibility(View v, boolean visible) {
        if (visible) v.setVisibility(VISIBLE);
        else v.setVisibility(GONE);
    }
}
