package io.ikws4.weiju.page.logcat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import io.ikws4.weiju.ext.MutableLiveDataExt;
import io.ikws4.weiju.page.BaseViewModel;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.Logger;
import io.ikws4.weiju.xposed.Console;

public class LogcatViewModel extends BaseViewModel {
    private final MutableLiveDataExt<String> mLogcatTag;

    public LogcatViewModel(@NonNull Application application) {
        super(application);
        mLogcatTag = new MutableLiveDataExt<>(mPreferences.get(Preferences.LOGCAT_TAG, Logger.TAG));
    }

    public LiveData<String> getLogcatTag() {
        return mLogcatTag;
    }

    public void switchToConsole() {
        mLogcatTag.setValue(Console.TAG);
        mPreferences.put(Preferences.LOGCAT_TAG, mLogcatTag.getValue());
    }

    public void switchToDebug() {
        mLogcatTag.setValue(Logger.TAG);
        mPreferences.put(Preferences.LOGCAT_TAG, mLogcatTag.getValue());
    }

    public void refresh() {
        mLogcatTag.publish();
    }
}
