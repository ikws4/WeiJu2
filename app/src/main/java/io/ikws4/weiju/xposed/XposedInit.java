package io.ikws4.weiju.xposed;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.widget.Toast;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.storage.scriptstore.XScriptStore;

public class XposedInit implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    /* package */ static XScriptStore store;
    /* package */ static WeakReference<Context> context;
    /* package */ static XC_LoadPackage.LoadPackageParam lpparam;

    private boolean scriptInjected;

    @Override
    public void initZygote(StartupParam startupParam) {
        XScriptStore.fixPermission();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedInit.lpparam = lpparam;

        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            updateHostXposedStatus();
            return;
        }

        XposedHelpers.findAndHookMethod(Instrumentation.class, "callApplicationOnCreate", Application.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Context ctx = (Context) (param.args[0]);
                context = new WeakReference<>(ctx);
                injectScripts(context.get());
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                if (scriptInjected) return;
                injectScripts(context.get());
                if (scriptInjected) return;
                Toast.makeText(context.get(), "Failed to load the scripts, please restart WeiJu2", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void injectScripts(Context context) {
        store = new XScriptStore(context);
        if (!store.canRead()) {
            return;
        }
        injectScriptsFor(BuildConfig.APPLICATION_ID); // global scripts
        injectScriptsFor(lpparam.packageName);
        scriptInjected = true;
    }

    private void injectScriptsFor(String pkgName) {
        Globals globals = XposedPlatform.create(pkgName);
        Set<String> keys = store.get(pkgName, Collections.emptySet());

        globals.set("lpparam", CoerceJavaToLua.coerce(lpparam));
        for (String key : keys) {
            String script = store.get(key, "");
            try {
                globals.load(script).call();
            } catch (Throwable e) {
                XposedBridge.log(e);
                Console.printErr(e);
            }
        }
    }

    private void updateHostXposedStatus() {
        Class<?> clazz = XposedHelpers.findClass("io.ikws4.weiju.WeiJu", lpparam.classLoader);
        XposedHelpers.setStaticBooleanField(clazz, "XPOSED_ENABLED", true);
    }
}
