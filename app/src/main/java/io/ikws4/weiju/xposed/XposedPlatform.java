package io.ikws4.weiju.xposed;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JseBaseLib;
import org.luaj.vm2.lib.jse.JseMathLib;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedPlatform {

    public static Globals create(XC_LoadPackage.LoadPackageParam lpparam) {
        Globals globals = new Globals();
        globals.set("lpparam", CoerceJavaToLua.coerce(lpparam));
        globals.load(new JseBaseLib());
        globals.load(new PackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        globals.load(new XposedLuajavaLib());
        globals.load(new XposedLib());
        globals.load(new JavaSyntaxLib());
        LoadState.install(globals);
        LuaC.install(globals);
        return globals;
    }
}
