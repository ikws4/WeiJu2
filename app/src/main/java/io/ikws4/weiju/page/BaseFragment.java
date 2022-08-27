package io.ikws4.weiju.page;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import io.ikws4.weiju.R;

public class BaseFragment extends Fragment implements MenuProvider {
    public static final int FRAGMENT_NORMAL = 0;
    public static final int FRAGMENT_FULL_SCREEN_DIALOG = 1;

    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requireAppCompatActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.STARTED);
    }

    public AppCompatActivity requireAppCompatActivity() {
        return (AppCompatActivity) requireActivity();
    }

    public ActionBar getSupportActionBar() {
        return requireAppCompatActivity().getSupportActionBar();
    }

    public void startFragment(Class<? extends BaseFragment> clazz) {
        startFragment(clazz, FRAGMENT_NORMAL);
    }

    public void startFragment(Class<? extends BaseFragment> clazz, int type) {
        startFragment(clazz, type, null);
    }

    public void startFragment(Class<? extends BaseFragment> clazz, int type, Bundle args) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
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

    @Override
    @CallSuper
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
