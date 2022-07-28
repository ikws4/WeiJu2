package io.ikws4.weiju.page.home;

import android.app.Application;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.ikws4.weiju.api.API;
import io.ikws4.weiju.ext.MutableLiveDataExt;
import io.ikws4.weiju.page.BaseViewModel;
import io.ikws4.weiju.page.home.widget.AppListView;
import io.ikws4.weiju.page.home.widget.ScriptListView;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.util.Logger;
import io.ikws4.weiju.util.RandomUtil;
import io.ikws4.weiju.util.Strings;
import io.ikws4.weiju.util.Template;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel {
    private final MutableLiveDataExt<List<AppListView.AppItem>> mSelectedApps = new MutableLiveDataExt<>(new ArrayList<>());
    private final MutableLiveDataExt<List<ScriptListView.ScriptItem>> mAvaliableScripts = new MutableLiveDataExt<>();
    private final MutableLiveDataExt<List<ScriptListView.ScriptItem>> mMyScripts = new MutableLiveDataExt<>();
    private final Globals mLuaGlobals;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mLuaGlobals = JsePlatform.standardGlobals();
        loadApplicationInfos();
        switchApp(mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, ""));
    }

    public LiveData<List<ScriptListView.ScriptItem>> getAvaliableScripts() {
        return mAvaliableScripts;
    }

    public LiveData<List<ScriptListView.ScriptItem>> getMyScripts() {
        return mMyScripts;
    }

    public LiveData<List<AppListView.AppItem>> getSelectedApps() {
        return mSelectedApps;
    }

    public ScriptListView.ScriptItem createNewScriptAndAddToMyScripts() {
        ScriptListView.ScriptItem item = null;
        try {
            InputStream in = getApplication().getAssets().open("script_template");
            Template template = new Template(in);
            template.set("name", RandomUtil.nextName(3));
            template.set("author", RandomUtil.nextName(1));
            template.set("version", "0.0.1");
            template.set("description", RandomUtil.nextName(10));
            addToMyScripts(item = ScriptListView.ScriptItem.from(template.toString()));
        } catch (IOException e) {
            Logger.d(e);
        }
        return item;
    }

    public void addApp(AppListView.AppItem app) {
        addApp(mSelectedApps.getValue().size(), app);
    }

    public void addApp(int index, AppListView.AppItem app) {
        Set<String> selected = new HashSet<>(mPreferences.get(Preferences.APP_LIST, Collections.emptySet()));
        selected.add(app.pkg + "," + System.currentTimeMillis());
        mPreferences.put(Preferences.APP_LIST, selected);

        List<AppListView.AppItem> infos = mSelectedApps.getValue();
        mSelectedApps.getValue().add(Math.min(index, infos.size()), app);
        mSelectedApps.publish();
    }

    public void switchApp(AppListView.AppItem app) {
        switchApp(app.pkg);
    }

    private void switchApp(String pkg) {
        mPreferences.put(Preferences.APP_LIST_SELECTED_PACKAGE, pkg);
        loadAvaliableScripts(pkg);
        loadMyScripts(pkg);
    }

    public void removeApp(AppListView.AppItem app) {
        Set<String> selected = new HashSet<>(mPreferences.get(Preferences.APP_LIST, Collections.emptySet()));
        selected.removeIf((it) -> it.split(",")[0].equals(app.pkg));
        mPreferences.put(Preferences.APP_LIST, selected);

        mSelectedApps.getValue().remove(app);
        mSelectedApps.publish();
    }

    public void updateSelectedAppAfterRemove(int index, AppListView.AppItem app) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        int n = mSelectedApps.getValue().size();
        if (n > 0 && app.pkg.equals(pkg)) {
            switchApp(mSelectedApps.getValue().get(Math.min(index, n - 1)));
        }
        mSelectedApps.publish();
    }

    public void addToMyScripts(ScriptListView.ScriptItem item) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        String key = Strings.join("&", pkg, item.id);

        Set<String> keys = new HashSet<>(mScriptStore.get(pkg, Collections.emptySet()));
        keys.add(key);

        mScriptStore.put(pkg, keys);
        mScriptStore.put(key, item.script);

        mMyScripts.getValue().add(item);
        mMyScripts.publish();
    }

    public void replaceInMyScripts(ScriptListView.ScriptItem oldItem, ScriptListView.ScriptItem newItem) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        String oldKey = getKey(oldItem);
        String newkey = getKey(newItem);

        Set<String> keys = new HashSet<>(mScriptStore.get(pkg, Collections.emptySet()));
        keys.remove(oldKey);
        keys.add(newkey);

        mScriptStore.put(pkg, keys);
        mScriptStore.put(oldKey, "");
        mScriptStore.put(newkey, newItem.script);


        int index = mMyScripts.getValue().indexOf(oldItem);
        mMyScripts.getValue().remove(index);
        mMyScripts.getValue().add(index, newItem);
        mMyScripts.publish();

        // reload avaliable script from server
        if (!oldItem.metadataEquals(newItem)) {
            loadAvaliableScripts(mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, ""));
        }
    }

    public void removeFromMyScripts(ScriptListView.ScriptItem item) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        String key = Strings.join("&", pkg, item.id);

        Set<String> keys = new HashSet<>(mScriptStore.get(pkg, Collections.emptySet()));
        keys.remove(key);

        mScriptStore.put(pkg, keys);
        mScriptStore.put(key, "");


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

        mDisposables.add(API.getInstance().getScopeConfig()
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

                mDisposables.add(Observable.fromIterable(avaliableScripts)
                    .map(script -> API.getInstance().getScript(script))
                    .observeOn(Schedulers.io())
                    .buffer(5, 5)
                    .subscribe(observables -> {
                            observables.stream().forEach(observable ->
                                observable.blockingSubscribe(contentFile -> {
                                    ScriptListView.ScriptItem item = ScriptListView.ScriptItem.from(contentFile.getContent());
                                    if (mMyScripts.getValue() == null || !isMyScriptsMetadataContains(item)) {
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
        Set<String> scriptKeys = mScriptStore.get(pkg, Collections.emptySet());

        mMyScripts.setValue(
            scriptKeys.stream()
                .map(key -> ScriptListView.ScriptItem.from(mScriptStore.get(key, "")))
                .collect(Collectors.toList())
        );
    }

    private void loadApplicationInfos() {
        PackageManager pm = getApplication().getPackageManager();
        Set<String> selectedAppWithTime = mPreferences.get(Preferences.APP_LIST, Collections.emptySet());
        Map<String, Long> map = selectedAppWithTime.stream()
            .collect(Collectors.toMap(it -> it.split(",")[0], it -> Long.valueOf(it.split(",")[1])));

        List<AppListView.AppItem> selectedData = new ArrayList<>();
        mDisposables.add(Observable.fromIterable(pm.getInstalledApplications(0))
            .subscribeOn(Schedulers.io())
            .filter(info -> map.containsKey(info.packageName))
            .sorted(Comparator.comparingLong(a -> map.get(a.packageName)))
            .map(info -> new AppListView.AppItem(info.loadLabel(pm).toString(), info.packageName, AppListView.AppItem.isSystemApp(info)))
            .buffer(5, 5)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(infos -> {
                selectedData.addAll(infos);
                mSelectedApps.setValue(selectedData);
            }));
    }

    private boolean isMyScriptsMetadataContains(ScriptListView.ScriptItem item) {
        if (mMyScripts.getValue() == null) return false;
        for (var script : mMyScripts.getValue()) {
            if (script.metadataEquals(item)) {
                return true;
            }
        }
        return false;
    }

    private String getKey(ScriptListView.ScriptItem item) {
        String pkg = mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, "");
        return Strings.join("&", pkg, item.id);
    }
}
