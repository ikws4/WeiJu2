package io.ikws4.weiju.xposed;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Build;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

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

        XposedHelpers.findAndHookMethod(Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Context context = (Context) param.args[0];

                Logger.d("===================", "WeiJu Debug Infos", "===================");
                Logger.d("AppName:", lpparam.appInfo.loadLabel(context.getPackageManager()));
                Logger.d("PackageName:", lpparam.packageName);
                Logger.d("Device:", Build.DEVICE);
                Logger.d("Android:", Build.VERSION.RELEASE);
                Logger.d("========================== END ==========================");

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

        String script = XScriptStore.getInstance(context).get(pkg);
        Globals globals = JsePlatform.standardGlobals();
        globals.set("lpparam", LuaValue.userdataOf(lpparam));
        globals.load(new XposedLib());
        Logger.d("Script:", script);
        globals.load(script).call();
    }

    private void updateHostXposedStatus() {
        Class<?> clazz = XposedHelpers.findClass("io.ikws4.weiju.page.main.MainActivity", classLoader);
        XposedHelpers.setStaticBooleanField(clazz, "XPOSED_ENABLED", true);
    }
}
