package io.ikws4.weiju.page.setting;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.ikws4.weiju.R;
import io.ikws4.weiju.WeiJu;
import io.ikws4.weiju.page.BaseViewModel;
import io.ikws4.weiju.utils.MutableLiveDataExt;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Action;

public class SettingViewModel extends BaseViewModel {
    private final String[] mDataXmls = new String[]{"script_store", "preference"};
    private final Gson mGson = new Gson();
    private final ClipboardManager mClipboardManager;
    private final MutableLiveDataExt<Boolean> mShowProgressBar = new MutableLiveDataExt<>(false);

    public SettingViewModel(@NonNull Application application) {
        super(application);
        mClipboardManager = WeiJu.getService(ClipboardManager.class);
    }

    public LiveData<Boolean> getShowProgressBar() {
        return mShowProgressBar;
    }

    public void backup() {
        delay(() -> {
            Map<String, Object> data = new HashMap<>();
            for (var xml : mDataXmls) {
                SharedPreferences pref = getApplication().getSharedPreferences(xml, Context.MODE_PRIVATE);
                data.put(xml, pref.getAll());
            }
            var json = mGson.toJson(data);

            // save to the clipboard
            mClipboardManager.setPrimaryClip(ClipData.newPlainText("WeiJu2 App Data", json));
            Toast.makeText(getApplication(), R.string.setting_saved_to_clipbard, Toast.LENGTH_SHORT).show();
        }, 750);
    }

    public void restore() {
        delay(() -> {
            // read from the clipboard
            var json = mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();

            // write to preferences
            try {
                Map<String, Object> data = mGson.fromJson(json, HashMap.class);
                for (var entry : data.entrySet()) {
                    var xml = entry.getKey();

                    if (Arrays.stream(mDataXmls).noneMatch((it) -> it.equals(xml))) {
                        continue;
                    }

                    var content = (Map<String, Object>) entry.getValue();
                    SharedPreferences pref = getPref(xml);
                    var editor = pref.edit();
                    editor.clear();
                    for (var contentEntry : content.entrySet()) {
                        var key = contentEntry.getKey();
                        var value = contentEntry.getValue();
                        if (value instanceof Boolean) {
                            editor.putBoolean(key, (boolean) value);
                        } else if (value instanceof Integer) {
                            editor.putInt(key, (int) value);
                        } else if (value instanceof Long) {
                            editor.putLong(key, (long) value);
                        } else if (value instanceof Float) {
                            editor.putFloat(key, (float) value);
                        } else if (value instanceof String) {
                            editor.putString(key, (String) value);
                        } else if (value instanceof List) {
                            editor.putStringSet(key, new HashSet((List<String>) value));
                        }
                    }
                    editor.apply();
                }
                Toast.makeText(getApplication(), R.string.setting_restore_succeed, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getApplication(), R.string.setting_restore_failed, Toast.LENGTH_LONG).show();
            }
        }, 750);
    }

    private SharedPreferences getPref(String name) {
        try {
            return getApplication().getSharedPreferences(name, Context.MODE_WORLD_READABLE);
        } catch (SecurityException e) {
            return getApplication().getSharedPreferences(name, Context.MODE_PRIVATE);
        }
    }

    private void delay(Action action, long ms) {
        mShowProgressBar.setValue(true);
        mDisposables.add(Single.timer(ms, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((it) -> {
                action.run();
                mShowProgressBar.setValue(false);
            }));
    }
}
