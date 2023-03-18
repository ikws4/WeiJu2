package io.ikws4.weiju.page.logcat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.BaseFragment;

public class LogcatFragment extends BaseFragment {
    private SwipeRefreshLayout vRefresher;

    private LogcatViewModel vm;

    public LogcatFragment() {
        super(R.layout.logcat_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle(R.string.logcat);

        TextView vLog = view.findViewById(R.id.tv_log);

        vm = new ViewModelProvider(requireActivity()).get(LogcatViewModel.class);
        vm.getLogs().observe(getViewLifecycleOwner(), log -> {
            vLog.setText(log);
            vRefresher.setRefreshing(false);
        });

        vRefresher = view.findViewById(R.id.refresher);
        vRefresher.setOnRefreshListener(() -> {
            vm.readLogs();
        });

        // auto-refresh when open logcat
        vm.readLogs();
        vRefresher.setRefreshing(true);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        super.onCreateMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.logcat_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.clear) {
            vRefresher.setRefreshing(true);
            vm.clearLogs();
        }

        return true;
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.logcat);
    }
}
