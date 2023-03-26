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
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

public class BaseFragment extends Fragment implements MenuProvider, IFragment {
    private Menu mMenu;

    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getMainActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.STARTED);
    }

    public MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    public ActionBar getSupportActionBar() {
        return getMainActivity().getSupportActionBar();
    }

    @Override
    @CallSuper
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        mMenu = menu;
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public String getFragmentTitle() {
        return getClass().getSimpleName().replace("Fragment", "");
    }

    @Override
    public String getFragmentSubtitle() {
        return null;
    }

    @Override
    public boolean isDisplayHomeAsUp() {
        return true;
    }

    public Menu getMenu() {
        return mMenu;
    }
}
