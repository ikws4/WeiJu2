package io.ikws4.weiju.widget.searchbar;

import android.content.pm.PackageManager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.page.home.widget.AppListView;
import io.ikws4.weiju.storage.Preferences;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UnselectedAppItemLoader extends SearchBar.ItemLoader {

    private Disposable mDisposable;

    @Override
    protected void load(SearchBar.ItemLoader.Callback callback) {
        Preferences preferences = Preferences.getInstance(getContext());

        Set<String> selectedAppWithTime = preferences.get(Preferences.APP_LIST, Collections.emptySet());
        Map<String, Long> map = selectedAppWithTime.stream()
            .collect(Collectors.toMap(it -> it.split(",")[0], it -> Long.valueOf(it.split(",")[1])));

        PackageManager pm = getContext().getPackageManager();
        mDisposable = Observable.fromIterable(pm.getInstalledApplications(0))
            .subscribeOn(Schedulers.io())
            .filter(info -> !info.packageName.equals(BuildConfig.APPLICATION_ID))
            .filter(info -> !map.containsKey(info.packageName))
            .sorted((a, b) -> {
                int res = Boolean.compare(AppListView.AppItem.isSystemApp(a), AppListView.AppItem.isSystemApp(b));
                if (res == 0) {
                    return a.packageName.compareTo(b.packageName);
                }
                return res;
            })
            .map(info -> new AppListView.AppItem(info.loadLabel(pm).toString(), info.packageName, AppListView.AppItem.isSystemApp(info)))
            .map(info -> new SearchBar.Item(info.name, info.imgUri, info))
            .buffer(5, 5)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(callback::send, callback::error, callback::finish);
    }

    @Override
    protected void clear() {
        super.clear();
        if (mDisposable != null) mDisposable.dispose();
    }
}
