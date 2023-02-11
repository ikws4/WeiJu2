package io.ikws4.weiju.page.logcat;

import android.app.Application;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.topjohnwu.superuser.Shell;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.ikws4.weiju.R;
import io.ikws4.weiju.utils.MutableLiveDataExt;
import io.ikws4.weiju.page.BaseViewModel;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.xposed.Console;

public class LogcatViewModel extends BaseViewModel {
    private final MutableLiveDataExt<CharSequence> mLogs;

    public LogcatViewModel(@NonNull Application application) {
        super(application);
        mLogs = new MutableLiveDataExt<>("");
    }

    public LiveData<CharSequence> getLogs() {
        return mLogs;
    }

    public void readLogs() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String time = mPreferences.get(Preferences.LOGCAT_TIME, getRebootTime());
        Shell.cmd("logcat -d -v tag -b main " + Console.TAG + ":D *:S -t '" + time + "'").submit(it -> {
            int errorColor = getApplication().getColor(R.color.rose);
            int debugColor = getApplication().getColor(R.color.foam);
            int textColor = getApplication().getColor(R.color.base);
            for (String line : it.getOut()) {
                var logline = LogLine.from(line);
                if (logline == null) continue;

                int startIndex = builder.length();

                builder.append(" ")
                    .append(logline.level)
                    .append(" ")
                    .append(logline.msg)
                    .append('\n');

                int color = logline.level.equals("D") ? debugColor : errorColor;

                builder.setSpan(new BackgroundColorSpan(color), startIndex, startIndex + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(textColor), startIndex, startIndex + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                builder.setSpan(new ForegroundColorSpan(color), startIndex + 3, startIndex + 3 + logline.msg.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            mLogs.setValue(builder);
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

    static class LogLine {
        public final String level;
        public final String msg;

        private LogLine(String level, String msg) {
            this.level = level;
            this.msg = msg;
        }

        public static LogLine from(String raw) {
            int startIndex = raw.indexOf(':');
            if (startIndex == -1) return null;

            String level = String.valueOf(raw.charAt(0));
            String msg = raw.substring(startIndex + 1).replaceAll("\t", "    ");
            return new LogLine(level, msg);
        }
    }
}
