package io.ikws4.weiju.page;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    public MainActivity() {
        super(R.layout.main_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, HomeFragment.class, null)
                .commit();
        }

        Toolbar vToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(vToolbar);
    }
}
