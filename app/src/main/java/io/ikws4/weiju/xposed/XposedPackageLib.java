package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.VarArgFunction;

class XposedPackageLib extends PackageLib {
    private final String mCurrentHookPackageName;

    public XposedPackageLib(String currrentHookPackageName) {
        mCurrentHookPackageName = currrentHookPackageName;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        super.call(modname, env);
        LuaTable searchers = package_.get("searchers").checktable();

        // rmeove builtin searchers to improve performance, we dont't need those
        searchers.set(1, new java_searcher());
        searchers.set(2, new xposed_script_searcher());
        searchers.set(3, NIL);
        return env;
    }

    public class java_searcher extends VarArgFunction {
        private String pathPrefix = "io.ikws4.weiju.xposed.lib.";

        public Varargs invoke(Varargs args) {
            String name = pathPrefix + args.checkjstring(1);
            String classname = toClassname( name );
            Class c = null;
            LuaValue v = null;
            try {
                c = Class.forName(classname);
                v = (LuaValue) c.newInstance();
                if (v.isfunction())
                    ((LuaFunction)v).initupvalue1(globals);
                return varargsOf(v, globals);
            } catch ( ClassNotFoundException  cnfe ) {
                return valueOf("\n\tno class '"+classname+"'" );
            } catch ( Exception e ) {
                return valueOf("\n\tjava load failed on '"+classname+"', "+e );
            }
        }
    }


    public class xposed_script_searcher extends VarArgFunction {
        public Varargs invoke(Varargs args) {
            LuaString name = args.checkstring(1);

            // First try to load builtin package if founded
            String script = BuiltinPackage.require(name.checkjstring());
            if (!script.isEmpty()) {
                LuaValue v = globals.load(script);
                if (v.isfunction())
                    return LuaValue.varargsOf(v, name);
            }

            // Seconds try to load user package
            script = XposedInit.store.get(mCurrentHookPackageName + "_" + name.tojstring(), "null");
            if (!script.equals("null")) {
                LuaValue v = globals.load(script);
                if (v.isfunction())
                    return LuaValue.varargsOf(v, name);
            }

            // report error
            return varargsOf(NIL, valueOf("'" + name + "': not found"));
        }
    }
}
