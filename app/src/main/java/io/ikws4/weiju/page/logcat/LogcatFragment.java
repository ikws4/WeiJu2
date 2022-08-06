package io.ikws4.weiju.page.logcat;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.BaseFragment;
import io.ikws4.weiju.util.Logger;

public class LogcatFragment extends BaseFragment {
    private LogcatViewModel vm;

    public LogcatFragment() {
        super(R.layout.logcat_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView vLog = view.findViewById(R.id.tv_log);
        SwipeRefreshLayout vRefresher = view.findViewById(R.id.refresher);
        vRefresher.setOnRefreshListener(() -> {
            vm.refresh();
        });

        vm = new ViewModelProvider(requireActivity()).get(LogcatViewModel.class);
        vm.getLogcatTag().observe(getViewLifecycleOwner(), tag -> {
            vLog.setText(readlog(tag));
            vRefresher.setRefreshing(false);
        });
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        super.onCreateMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.logcat_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.console) {
            vm.switchToConsole();
        } else if (id == R.id.debug) {
            vm.switchToDebug();
        } else if (id == R.id.clear) {
            clearlog();
            vm.refresh();
        } else {
            return false;
        }

        return true;
    }

    private void clearlog() {
        try {
            logcat("-c");
        } catch (IOException e) {
            Logger.e(e);
        }
    }

    private CharSequence readlog(String tag) {
        try {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Process logcat = logcat("-d -v tag -b main " + tag + ":D *:S");
            BufferedReader reader = new BufferedReader(new InputStreamReader(logcat.getInputStream()));

            int errorColor = getContext().getColor(R.color.love);
            int debugColor = getContext().getColor(R.color.foam);
            int textColor = getContext().getColor(R.color.surface);
            reader.lines().forEach((line) -> {
                var logline = LogLine.from(line);
                int startIndex = builder.length();

                builder.append(" ")
                    .append(logline.level)
                    .append(" ")
                    .append(logline.msg)
                    .append('\n');

                BackgroundColorSpan span;
                if (logline.level.equals("D")) {
                    span = new BackgroundColorSpan(debugColor);
                } else {
                    span = new BackgroundColorSpan(errorColor);
                }

                builder.setSpan(span, startIndex, startIndex + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(textColor), startIndex, startIndex + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            });
            return builder;
        } catch (IOException e) {
            Logger.e(e);
        }
        return "";
    }

    private Process logcat(String args) throws IOException {
        return Runtime.getRuntime().exec("su -c logcat " + args);
    }

    static class LogLine {
        public final String level;
        public final String msg;

        private LogLine(String level, String msg) {
            this.level = level;
            this.msg = msg;
        }

        public static LogLine from(String raw) {
            int startIndex = 0;
            while (raw.charAt(startIndex) != ':') startIndex++;
            String level = String.valueOf(raw.charAt(0));
            String msg = raw.substring(startIndex + 1).replaceAll("\t", "    ");
            return new LogLine(level, msg);
        }
    }
}
