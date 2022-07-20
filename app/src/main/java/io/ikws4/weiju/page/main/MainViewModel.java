package io.ikws4.weiju.page.main;

import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.storage.Preferences;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<List<AppInfo>> selectedAppInfos = new MutableLiveData<>(new ArrayList<>());

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

    public void selectApp(AppInfo info) {
        String pkg = info.pkg;
        Set<String> selected = new HashSet<>(mPreferences.get(Preferences.APP_LIST, (a) -> new HashSet<>()));
        selected.add(pkg + "," + System.currentTimeMillis());
        mPreferences.put(Preferences.APP_LIST, selected);
        mPreferences.put(Preferences.APP_LIST_SELECTED_PACKAGE, pkg);

        List<AppInfo> infos = selectedAppInfos.getValue();
        infos.add(info);
        selectedAppInfos.setValue(infos);
    }

    private void loadApplicationInfos() {
        PackageManager pm = getApplication().getPackageManager();
        Set<String> selectedAppWithTime = mPreferences.get(Preferences.APP_LIST, (a) -> new HashSet<>());
        Map<String, Long> map = selectedAppWithTime.stream()
            .collect(Collectors.toMap(it -> it.split(",")[0], it -> Long.valueOf(it.split(",")[1])));

        List<AppInfo> selectedData = new ArrayList<>();
        disposables.add(Observable.fromIterable(pm.getInstalledApplications(0))
            .subscribeOn(Schedulers.io())
            .filter(info -> map.containsKey(info.packageName))
            .sorted(Comparator.comparingLong(a -> map.get(a.packageName)))
            .map(info -> new AppInfo(info.loadLabel(pm).toString(), info.packageName, AppInfo.isSystemApp(info)))
            .buffer(5, 5)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(infos -> {
                selectedData.addAll(infos);
                selectedAppInfos.setValue(selectedData);
            }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}