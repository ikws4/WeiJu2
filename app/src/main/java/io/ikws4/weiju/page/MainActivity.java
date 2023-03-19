package io.ikws4.weiju.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.topjohnwu.superuser.Shell;

import io.ikws4.weiju.R;

public class MainActivity extends AppCompatActivity {
    public static final int FRAGMENT_NORMAL = 0;
    public static final int FRAGMENT_FULL_SCREEN_DIALOG = 1;
    private MainViewModel vm;

    public MainActivity() {
        super(R.layout.main_activity);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(this).get(MainViewModel.class);

        setupActionBar();
        setupProgressBar();
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateActionBar);

        ensurePermissions();
    }

    private void setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setupProgressBar() {
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

    private void ensurePermissions() {
        // root permission
        Shell.getShell();

        if (!Shell.rootAccess()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Dialog_WeiJu)
                .setTitle(R.string.main_permission_request)
                .setCancelable(false)
                .setMessage(R.string.main_permission_request_root_description)
                .setPositiveButton(android.R.string.ok, null);
            builder.getBackground().setTint(getColor(R.color.base));
            builder.show();
        }
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

    public void startFragment(Class<? extends Fragment> clazz) {
        startFragment(clazz, FRAGMENT_NORMAL);
    }

    public void startFragment(Class<? extends Fragment> clazz, int type) {
        startFragment(clazz, type, null);
    }

    public void startFragment(Class<? extends Fragment> clazz, int type, Bundle args) {
        FragmentTransaction transaction = getSupportFragmentManager()
            .beginTransaction()
            .setReorderingAllowed(true);

        if (type == FRAGMENT_NORMAL) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        } else if (type == FRAGMENT_FULL_SCREEN_DIALOG) {
            transaction.setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_top,
                R.anim.slide_in_bottom,
                R.anim.slide_out_top
            );
        }

        transaction.add(R.id.fragment_container, clazz, args)
            .addToBackStack(null)
            .commit();
    }

    private void updateActionBar() {
        var fragment = (IFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(fragment.isDisplayHomeAsUp());
            getSupportActionBar().setTitle(fragment.getFragmentTitle());
            getSupportActionBar().setSubtitle(fragment.getFragmentSubtitle());
        }
    }
}
