package io.ikws4.weiju.page;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.ikws4.weiju.storage.Preferences;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseViewModel extends AndroidViewModel {
    protected final CompositeDisposable mDisposables = new CompositeDisposable();
    protected final Preferences mPreferences;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mPreferences = Preferences.getInstance(getApplication());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposables.clear();
    }
}
