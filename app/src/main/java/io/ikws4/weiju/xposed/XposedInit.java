package io.ikws4.weiju.xposed;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import org.luaj.vm2.Globals;

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
    /* package */ static ClassLoader classloader;
    /* package */ static XScriptStore store;
    /* package */ static String currnetPackageName;
    /* package */ static WeakReference<Context> context;

    @Override
    public void initZygote(StartupParam startupParam) {
        XScriptStore.fixPermission();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        classloader = lpparam.classLoader;
        currnetPackageName = lpparam.packageName;

        if (currnetPackageName.equals(BuildConfig.APPLICATION_ID)) {
            updateHostXposedStatus();
            return;
        }

        XposedHelpers.findAndHookMethod(Application.class, "onCreate", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Context application = (Application) param.thisObject;
                context = new WeakReference<>(application);
                injectScripts(application);
            }
        });
    }

    public void injectScripts(Context context) {
        Globals globals = XposedPlatform.create();
        store = XScriptStore.getInstance(context);

        if (!store.canRead()) {
            Toast.makeText(context, "Failed to load the scripts, please restart WeiJu2", Toast.LENGTH_LONG).show();
            return;
        }

        Set<String> keys = store.get(currnetPackageName, Collections.emptySet());
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
        Class<?> clazz = XposedHelpers.findClass("io.ikws4.weiju.WeiJu", classloader);
        XposedHelpers.setStaticBooleanField(clazz, "XPOSED_ENABLED", true);
    }
}
