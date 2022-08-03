package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

class JavaSyntaxSugarLib extends TwoArgFunction {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        env.set("import", new _import(env));
        env.set("new", new _new(env));
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
            return  _G.get("luajava").get("bindClass").call(pkg);
        }
    }

    static final class _new extends OneArgFunction {
        private final LuaValue _G;

        _new(LuaValue g) {
            _G = g;
        }

        @Override
        public LuaValue call(LuaValue arg) {
            String pkg = arg.checkjstring();
            return  _G.get("luajava").get("new").call(pkg);
        }
    }
}
