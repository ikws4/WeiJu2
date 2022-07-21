package io.ikws4.weiju.page;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import io.ikws4.weiju.R;

public class MainActivity extends FragmentActivity {
    // For xposed to hook this variable to indicate
    // that xposed works.
    private static boolean XPOSED_ENABLED = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.getMenu().findItem(R.id.xposed_status).setVisible(!XPOSED_ENABLED);
        toolbar.setOnMenuItemClickListener((menu) -> {
            if (menu.getItemId() == R.id.xposed_status) {
                Toast.makeText(this, "WeiJu was not enabled in xposed.", Toast.LENGTH_SHORT).show();
            } else if(menu.getItemId() == R.id.settings) {
                Toast.makeText(this, "TODO: Settings", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }
}
