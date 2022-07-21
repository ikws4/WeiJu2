package io.ikws4.weiju.page.main;

import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.ikws4.weiju.api.API;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.ext.MutableLiveDataExt;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.storage.ScriptStore;
import io.ikws4.weiju.util.Logger;
import io.ikws4.weiju.widget.view.ScriptListView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveDataExt<List<AppInfo>> mSelectedAppInfos = new MutableLiveDataExt<>(new ArrayList<>());
    private final MutableLiveDataExt<List<ScriptListView.ScriptItem>> mAvaliableScripts = new MutableLiveDataExt<>();
    private final MutableLiveDataExt<List<ScriptListView.ScriptItem>> mMyScripts = new MutableLiveDataExt<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Preferences mPreferences;
    private final ScriptStore mScriptStore;
    private final Globals mLuaGlobals;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mLuaGlobals = JsePlatform.standardGlobals();
        mPreferences = Preferences.getInstance(getApplication());
        mScriptStore = ScriptStore.getInstance(getApplication());
        loadApplicationInfos();
        switchApp(mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, ""));
    }

    public LiveData<List<ScriptListView.ScriptItem>> getAvaliableScripts() {
        return mAvaliableScripts;
    }

    public LiveData<List<ScriptListView.ScriptItem>> getMyScripts() {
        return mMyScripts;
    }

    public LiveData<List<AppInfo>> getSelectedAppInfos() {
        return mSelectedAppInfos;
    }

    public void selectApp(AppInfo info) {
        String pkg = info.pkg;
        switchApp(pkg);

        Set<String> selected = new HashSet<>(mPreferences.get(Preferences.APP_LIST, (a) -> new HashSet<>()));
        selected.add(pkg + "," + System.currentTimeMillis());
        mPreferences.put(Preferences.APP_LIST, selected);

        List<AppInfo> infos = mSelectedAppInfos.getValue();
        infos.add(info);
        mSelectedAppInfos.setValue(infos);
    }

    public void switchApp(String pkg) {
        mPreferences.put(Preferences.APP_LIST_SELECTED_PACKAGE, pkg);
        loadAvaliableScripts(pkg);
        loadMyScripts(pkg);
    }

    public void addToMyScript(ScriptListView.ScriptItem item) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");

        Set<String> ids = new HashSet<>(mScriptStore.get(pkg, (v) -> new HashSet<>()));
        ids.add(item.id);
        mScriptStore.put(pkg, ids);
        mScriptStore.put(item.id, item.script);

        mMyScripts.getValue().add(item);
        mMyScripts.publish();
    }

    public void removeFromMyScripts(ScriptListView.ScriptItem item) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");

        Set<String> ids = new HashSet<>(mScriptStore.get(pkg, (v) -> new HashSet<>()));
        ids.remove(item.id);
        mScriptStore.put(pkg, ids);
        mScriptStore.put(item.id, "");

        mMyScripts.getValue().remove(item);
        mMyScripts.publish();

        // reload avaliable script from server
        loadAvaliableScripts(mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, ""));
    }

    public void removeFromAvaliableScripts(ScriptListView.ScriptItem item) {
        mAvaliableScripts.getValue().remove(item);
        mAvaliableScripts.publish();
    }

    private void loadAvaliableScripts(String pkg) {
        mAvaliableScripts.setValue(null);

        disposables.add(API.getInstance().getScopeConfig()
            .subscribeOn(Schedulers.io())
            .subscribe(it -> {
                // Get all avaliable scripts for this pkg
                List<String> avaliableScripts = new ArrayList<>();
                String scopeConfigLuaCode = it.getContent();
                LuaTable config = mLuaGlobals.load(scopeConfigLuaCode).call().checktable();
                for (LuaValue key : config.keys()) {
                    LuaValue scope = config.get(key);
                    if (scope.isstring()) {
                        if (pkg.matches(scope.checkjstring())) {
                            avaliableScripts.add(key.checkjstring());
                        }
                    } else if (scope.istable()) {
                        LuaTable _scope = (LuaTable) scope;
                        for (int i = 0; i < _scope.keyCount(); i++) {
                            String v = _scope.get(i).checkjstring();
                            if (pkg.matches(v)) {
                                avaliableScripts.add(v);
                            }
                        }
                    }
                }

                // Fetch scritps contents
                List<ScriptListView.ScriptItem> scriptItems = new ArrayList<>();

                disposables.add(Observable.fromIterable(avaliableScripts)
                    .map(script -> API.getInstance().getScript(script))
                    .observeOn(Schedulers.io())
                    .buffer(5, 5)
                    .subscribe(observables -> {
                            observables.stream().forEach(observable ->
                                observable.blockingSubscribe(contentFile -> {
                                    ScriptListView.ScriptItem item = ScriptListView.ScriptItem.from(contentFile.getContent());
                                    if (mMyScripts.getValue() == null || !mMyScripts.getValue().contains(item)) {
                                        scriptItems.add(item);
                                    }
                                }, Logger::e));
                        },
                        Logger::e,
                        () -> {
                            mAvaliableScripts.postValue(scriptItems);
                        }
                    ));
            }, Logger::e));
    }

    private void loadMyScripts(String pkg) {
        Set<String> scriptIds = mScriptStore.get(pkg, (v) -> new HashSet<>());

        mMyScripts.setValue(
            scriptIds.stream()
                .map(id -> ScriptListView.ScriptItem.from(mScriptStore.get(id, "")))
                .collect(Collectors.toList())
        );
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
                mSelectedAppInfos.setValue(selectedData);
            }));
    }

    private boolean filterOutInMyScriptsItem(String id) {
        List<ScriptListView.ScriptItem> items = mMyScripts.getValue();
        if (items != null) {
            for (ScriptListView.ScriptItem item : items) {
                if (item.id.equals(id)) return false;
            }
        }
        return true;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
