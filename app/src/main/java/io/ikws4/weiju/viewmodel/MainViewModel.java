package io.ikws4.weiju.viewmodel;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.ikws4.weiju.data.AppInfo;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private MutableLiveData<List<AppInfo>> appInfos;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<AppInfo>> getAppInfos() {
        if (appInfos == null) {
            appInfos = new MutableLiveData<>();
            loadApplicationInfos();
        }
        return appInfos;
    }

    private void loadApplicationInfos() {
        PackageManager pm = getApplication().getPackageManager();

        disposables.add(Observable.fromIterable(pm.getInstalledApplications(0))
            .subscribeOn(Schedulers.io())
            .filter(info -> (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            .map((info) -> new AppInfo(info.loadLabel(pm), info.packageName, info))
            .buffer(5, 5)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(infos -> {
                appInfos.setValue(infos);
            }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
