package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.VarArgFunction;

class XposedPackageLib extends PackageLib {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        super.call(modname, env);
        LuaTable searchers = package_.get("searchers").checktable();

        // rmeove builtin searchers to improve performance, we dont't need those
        searchers.set(1, NIL);
        searchers.set(2, NIL);
        searchers.set(3, NIL);

        searchers.set(1, new xposed_script_searcher());
        return env;
    }

    public class xposed_script_searcher extends VarArgFunction {
        public Varargs invoke(Varargs args) {
            LuaString name = args.checkstring(1);

            // First try to load user package
            String script = XposedInit.store.get(XposedInit.currnetPackageName + "_" + name.tojstring(), "null");
            if (!script.equals("null")) {
                LuaValue v = globals.load(script);
                if (v.isfunction())
                    return LuaValue.varargsOf(v, name);
            }


            // Second try to load builtin package if founded
            script = BuiltinPackage.require(name.checkjstring());
            if (!script.isEmpty()) {
                LuaValue v = globals.load(script);
                if (v.isfunction())
                    return LuaValue.varargsOf(v, name);

            }

            // report error
            return varargsOf(NIL, valueOf("'" + name + "': not found"));
        }
    }
}
