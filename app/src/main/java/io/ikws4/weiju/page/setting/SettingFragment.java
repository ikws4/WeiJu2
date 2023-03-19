package io.ikws4.weiju.page.setting;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.IFragment;
import io.ikws4.weiju.page.MainActivity;
import io.ikws4.weiju.page.MainViewModel;
import io.ikws4.weiju.page.about.AboutFragment;
import io.ikws4.weiju.page.home.HomeViewModel;

public class SettingFragment extends PreferenceFragmentCompat implements MenuProvider, Preference.OnPreferenceClickListener, IFragment {
    protected SettingViewModel vm;
    protected MainViewModel mainVM;
    protected HomeViewModel homeVM;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(getContext().getColor(R.color.base));

        vm = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        mainVM = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        homeVM = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        getMainActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.STARTED);

        findPreference(getString(R.string.backup)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.restore)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.about)).setOnPreferenceClickListener(this);

        vm.getShowProgressBar().observe(getViewLifecycleOwner(), (it) -> {
            if (it) mainVM.showProgressBar();
            else mainVM.hideProgressBar();
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeVM.refresh();
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        var key = preference.getKey();
        if (key.equals(getString(R.string.backup))) {
            vm.backup();
        } else if (key.equals(getString(R.string.restore))) {
            vm.restore();
        } else if (key.equals(getString(R.string.about))) {
            getMainActivity().startFragment(AboutFragment.class);
        }
        return false;
    }

    public MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.settings);
    }

    @Override
    public String getFragmentSubtitle() {
        return null;
    }

    @Override
    public boolean isDisplayHomeAsUp() {
        return true;
    }
}
