package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

class JavaSyntaxLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        env.set("import", new _import(env));
        return env;
    }

    static final class _import extends OneArgFunction {
        private final LuaValue _G;

        _import(LuaValue g) {
            _G = g;
        }

        @Override
        public LuaValue call(LuaValue arg) {
            String pkg = arg.checkjstring();
            String identifier = pkg.substring(pkg.lastIndexOf(".") + 1);
            LuaValue clazz = _G.get("luajava").get("bindClass").call(pkg);
            _G.set(identifier, clazz);
            return NIL;
        }
    }

}
