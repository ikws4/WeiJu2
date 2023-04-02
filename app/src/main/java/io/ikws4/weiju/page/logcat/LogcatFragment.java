package io.ikws4.weiju.page.logcat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.ikws4.weiju.R;
import io.ikws4.weiju.page.BaseFragment;

public class LogcatFragment extends BaseFragment {
    private SwipeRefreshLayout vRefresher;

    private LogcatViewModel vm;
    private LogAdapter mAdapter;
    private RecyclerView mVLog;

    public LogcatFragment() {
        super(R.layout.logcat_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new LogAdapter();
        mVLog = view.findViewById(R.id.v_log);
        mVLog.setAdapter(mAdapter);

        vm = new ViewModelProvider(requireActivity()).get(LogcatViewModel.class);
        vm.getLogs().observe(getViewLifecycleOwner(), log -> {
            mAdapter.submitList(log);
            vRefresher.setRefreshing(false);
        });

        vRefresher = view.findViewById(R.id.refresher);
        vRefresher.setOnRefreshListener(() -> {
            vm.readLogs();
        });

        mVLog.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private MenuItem scrollToTop;
            private MenuItem scrollToBottom;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                refreshMenuIcon(recyclerView);
            }

            private void refreshMenuIcon(@NonNull RecyclerView recyclerView) {
                final int offset = recyclerView.computeVerticalScrollOffset();
                final int range = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();

                if (scrollToTop == null) {
                    if (getMenu() == null) return;
                    scrollToTop = getMenu().findItem(R.id.scroll_to_top);
                    scrollToBottom = getMenu().findItem(R.id.scroll_to_bottom);
                    return;
                }

                var onlyShowScrollToTop = 2 * offset >= range;
                scrollToTop.setVisible(onlyShowScrollToTop);
                scrollToBottom.setVisible(!onlyShowScrollToTop);
            }
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
            clearLog();
        } else if (id == R.id.scroll_to_top) {
            scrollToTop();
        } else if (id == R.id.scroll_to_bottom) {
            scrollToBottom();
        }

        return true;
    }

    private void scrollToBottom() {
        var last = mVLog.getAdapter().getItemCount() - 1;
        if (last >= 0) scrollToPosition(last);
    }

    private void scrollToTop() {
        scrollToPosition(0);
    }

    private void clearLog() {
        vRefresher.setRefreshing(true);
        vm.clearLogs();
    }

    private void scrollToPosition(int pos) {
        final int range = mVLog.computeVerticalScrollRange() - mVLog.computeVerticalScrollExtent();
        if (range < 5 * getView().getHeight()) {
            mVLog.smoothScrollToPosition(pos);
        } else {
            mVLog.scrollToPosition(pos);
        }
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.logcat);
    }

    static class LogAdapter extends ListAdapter<LogcatViewModel.LogItem, LogAdapter.ViewHolder> {

        private static final DiffUtil.ItemCallback<LogcatViewModel.LogItem> sCallback = new DiffUtil.ItemCallback<>() {
            @Override
            public boolean areItemsTheSame(@NonNull LogcatViewModel.LogItem oldItem, @NonNull LogcatViewModel.LogItem newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull LogcatViewModel.LogItem oldItem, @NonNull LogcatViewModel.LogItem newItem) {
                return oldItem.equals(newItem);
            }
        };

        protected LogAdapter() {
            super(sCallback);
        }

        @NonNull
        @Override
        public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            var view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(getItem(position));
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final int mErrorColor;
            private final int mDebugColor;
            private final int mTextColor;
            private final TextView vText;
            private final SpannableStringBuilder mBuilder;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                var context = itemView.getContext();
                mErrorColor = withAlpha(context.getColor(R.color.love), 0.9f);
                mDebugColor = withAlpha(context.getColor(R.color.foam), 0.9f);
                mTextColor = context.getColor(R.color.base);
                vText = itemView.findViewById(R.id.v_text);
                mBuilder = new SpannableStringBuilder();
            }

            public void bind(LogcatViewModel.LogItem item) {
                var level = item.level;
                var msg = item.msg;

                mBuilder.clear();
                mBuilder.append(" ").append(level).append(" ").append(msg);

                int color = level.equals("D") ? mDebugColor : mErrorColor;

                mBuilder.setSpan(new BackgroundColorSpan(color), 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                mBuilder.setSpan(new ForegroundColorSpan(mTextColor), 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                vText.setBackgroundColor(withAlpha(color, 0.1f));
                vText.setTextColor(color);
                vText.setText(mBuilder);
            }

            private int withAlpha(int color, float alpha) {
                // argb
                int a = (int) (alpha * 255);
                return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
            }
        }
    }
}
