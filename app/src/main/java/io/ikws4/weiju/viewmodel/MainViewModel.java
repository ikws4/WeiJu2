package io.ikws4.weiju.viewmodel;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.storage.Preferences;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<List<AppInfo>> selectedAppInfos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<AppInfo>> unSelectedAppInfos = new MutableLiveData<>(new ArrayList<>());

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final Preferences mPreferences;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mPreferences = Preferences.getInstance(getApplication());
        loadApplicationInfos();
    }

    public LiveData<List<AppInfo>> getSelectedAppInfos() {
        return selectedAppInfos;
    }

    public LiveData<List<AppInfo>> getUnSelectedAppInfos() {
        return unSelectedAppInfos;
    }

    public void selectApp(String pkg) {
        Set<String> selected = new HashSet<>(mPreferences.get(Preferences.APP_LIST, (a) -> new HashSet<>()));
        selected.add(pkg);
        mPreferences.put(Preferences.APP_LIST, selected);

        unSelectedAppInfos.getValue()
            .stream()
            .filter(it -> it.pkg.equals(pkg))
            .findFirst()
            .ifPresent(info -> {
                List<AppInfo> infos = selectedAppInfos.getValue();
                infos.add(info);
                selectedAppInfos.setValue(new ArrayList<>(infos));

                infos = unSelectedAppInfos.getValue();
                infos.remove(info);
                unSelectedAppInfos.setValue(infos);
            });
    }

    private void loadApplicationInfos() {
        loadApplicationInfos(true, selectedAppInfos);
        loadApplicationInfos(false, unSelectedAppInfos);
    }

    private void loadApplicationInfos(boolean isLoadSelected, MutableLiveData<List<AppInfo>> liveData) {
        PackageManager pm = getApplication().getPackageManager();
        Set<String> selected = mPreferences.get(Preferences.APP_LIST, (a) -> new HashSet<>());

        List<AppInfo> data = new ArrayList<>();
        disposables.add(Observable.fromIterable(pm.getInstalledApplications(0))
            .subscribeOn(Schedulers.io())
            .filter(info -> selected.contains(info.packageName) == isLoadSelected)
            .map((info) -> new AppInfo(info.loadLabel(pm).toString(), info.packageName, isSystemApp(info)))
            .sorted((a, b) -> Boolean.compare(a.isSystemApp, b.isSystemApp))
            .buffer(5, 5)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(infos -> {
                data.addAll(infos);
                liveData.setValue(data);
            }));
    }

    private boolean isSystemApp(ApplicationInfo info) {
        return (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
