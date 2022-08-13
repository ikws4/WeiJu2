package io.ikws4.weiju.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    public MainActivity() {
        super(R.layout.main_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainViewModel vm = new ViewModelProvider(this).get(MainViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container, HomeFragment.class, null)
                .commit();
        }

        Toolbar vToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(vToolbar);

        LinearProgressIndicator vProgressBar = findViewById(R.id.progress_bar);
        vm.getProgressBarStatus().observe(this, visible -> {
            if (visible) {
                vProgressBar.setVisibility(View.VISIBLE);
            } else {
                vProgressBar.animate()
                    .setDuration(350)
                    .alpha(0)
                    .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                vProgressBar.setVisibility(View.GONE);
                                vProgressBar.setAlpha(1);
                            }
                        }
                    );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(null);
    }
}
