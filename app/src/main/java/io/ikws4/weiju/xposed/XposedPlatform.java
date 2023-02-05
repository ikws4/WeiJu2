package io.ikws4.weiju.xposed;

import android.util.Log;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseMathLib;

public class XposedPlatform {
    public static Globals create(String pkgName) {
        Globals globals = new Globals();

        // java impl libs
        globals.load(new XposedBaselib());
        globals.load(new XposedPackageLib(pkgName));
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        globals.load(new XposedLuajavaLib());

        // redirect output stream to console
        globals.STDOUT = new Console.LogginStream(Log.DEBUG);
        globals.STDERR = new Console.LogginStream(Log.ERROR);

        // init compiler
        LoadState.install(globals);
        LuaC.install(globals);

        // lua libs
        globals.load(BuiltinPackage.require("java_common_syntax")).call();
        globals.load(BuiltinPackage.require("java_common_types")).call();
        globals.load(BuiltinPackage.require("table_util")).call();
        globals.load(BuiltinPackage.require("string_util")).call();

        return globals;
    }
}
