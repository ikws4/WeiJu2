package io.ikws4.weiju.xposed;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import org.luaj.vm2.Globals;

import java.util.Collections;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.storage.XScriptStore;
import io.ikws4.weiju.util.Logger;

public class XposedInit implements IXposedHookLoadPackage {
    /* package */ static ClassLoader classLoader;


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        classLoader = lpparam.classLoader;

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Context context = (Context) param.thisObject;

                Logger.d("DEBUG INFOS");
                Logger.d("  AppName:", lpparam.appInfo.loadLabel(context.getPackageManager()));
                Logger.d("  PackageName:", lpparam.packageName);
                Logger.d("  DeviceInfo:", Build.DEVICE);
                Logger.d("  AndroidVersion:", Build.VERSION.RELEASE);

                onApplicationCreated(lpparam, context);
            }
        });
    }

    public void onApplicationCreated(XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        String pkg = lpparam.packageName;

        if (pkg.equals(BuildConfig.APPLICATION_ID)) {
            updateHostXposedStatus();
            return;
        }

        Globals globals = XposedPlatform.create(lpparam);
        XScriptStore store = XScriptStore.getInstance(context);
        Set<String> keys = store.get(pkg, Collections.emptySet());
        for (String key : keys) {
            String script = store.get(key, "");
            try {
                globals.load(script).call();
            } catch (Throwable e) {
                Console.printErr(e);
            }
        }
    }

    private void updateHostXposedStatus() {
        Class<?> clazz = XposedHelpers.findClass("io.ikws4.weiju.page.home.HomeFragment", classLoader);
        XposedHelpers.setStaticBooleanField(clazz, "XPOSED_ENABLED", true);
    }
}
