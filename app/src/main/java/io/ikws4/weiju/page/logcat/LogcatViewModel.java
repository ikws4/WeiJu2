package io.ikws4.weiju.page.logcat;

import android.app.Application;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.topjohnwu.superuser.Shell;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.ikws4.weiju.page.BaseViewModel;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.utils.MutableLiveDataExt;
import io.ikws4.weiju.xposed.Console;

public class LogcatViewModel extends BaseViewModel {
    private final MutableLiveDataExt<List<LogItem>> mLogs;
    private Executor mExecutor;

    public LogcatViewModel(@NonNull Application application) {
        super(application);
        mLogs = new MutableLiveDataExt<>();
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<LogItem>> getLogs() {
        return mLogs;
    }

    public void readLogs() {
        String time = mPreferences.get(Preferences.LOGCAT_TIME, getRebootTime());
        List<LogItem> logItems = new ArrayList<>();
        Shell.cmd("logcat -d -v tag -b main " + Console.TAG + ":D *:S -t '" + time + "'").submit(Executors.newSingleThreadExecutor(), it -> {
            for (String line : it.getOut()) {
                var logitem = LogItem.from(line);
                if (logitem != null) {
                    logItems.add(logitem);
                }
            }
            mLogs.postValue(logItems);
        });
    }

    public void clearLogs() {
        mPreferences.put(Preferences.LOGCAT_TIME, getCurrentTime());
        readLogs();
    }

    private static final SimpleDateFormat LOGCAT_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.s", Locale.US);

    private String getRebootTime() {
        return getTime(SystemClock.elapsedRealtime());
    }

    private String getCurrentTime() {
        return getTime(System.currentTimeMillis());
    }

    private String getTime(long t) {
        return LOGCAT_DATE_FORMAT.format(new Date(t));
    }

    // private Process logcat(String args) throws IOException {
    //     Shell.cmd("logcat ")
    // }

    public static class LogItem {
        public final String level;
        public final String msg;

        private LogItem(String level, String msg) {
            this.level = level;
            this.msg = msg;
        }

        public static LogItem from(String raw) {
            int startIndex = raw.indexOf(':');
            if (startIndex == -1) return null;

            String level = String.valueOf(raw.charAt(0));
            String msg = raw.substring(startIndex + 1).replaceAll("\t", "    ");
            return new LogItem(level, msg);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LogItem item = (LogItem) o;
            return Objects.equals(level, item.level) && Objects.equals(msg, item.msg);
        }

        @Override
        public int hashCode() {
            return Objects.hash(level, msg);
        }
    }
}
