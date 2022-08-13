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
import androidx.lifecycle.Lifecycle;

public class BaseFragment extends Fragment implements MenuProvider {

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
