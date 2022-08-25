package io.ikws4.weiju.page.logcat;

import android.app.Application;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.ikws4.weiju.R;
import io.ikws4.weiju.ext.MutableLiveDataExt;
import io.ikws4.weiju.page.BaseViewModel;
import io.ikws4.weiju.storage.Preferences;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        mDisposables.add(Completable.complete()
            .subscribeOn(Schedulers.io())
            .subscribe(() -> {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                Process logcat = logcat("-d -v tag -b main Console:D *:S -t '" + mPreferences.get(Preferences.LOGCAT_TIME, getRebootTime()) + "'");
                BufferedReader reader = new BufferedReader(new InputStreamReader(logcat.getInputStream()));

                int errorColor = getApplication().getColor(R.color.love);
                int debugColor = getApplication().getColor(R.color.foam);
                int textColor = getApplication().getColor(R.color.surface);
                reader.lines().forEach((line) -> {
                    var logline = LogLine.from(line);
                    if (logline == null) return;

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

                mLogs.postValue(builder);
            }));
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
                int startIndex = raw.indexOf(':');
                if (startIndex == -1) return null;

                String level = String.valueOf(raw.charAt(0));
                String msg = raw.substring(startIndex + 1).replaceAll("\t", "    ");
                return new LogLine(level, msg);
        }
    }
}
