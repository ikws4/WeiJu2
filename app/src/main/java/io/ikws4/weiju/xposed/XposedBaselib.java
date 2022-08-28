package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.luaj.vm2.lib.jse.JseBaseLib;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;

class XposedBaselib extends JseBaseLib {

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        super.call(modname, env);
        env.set("hook", new hook());
        return env;
    }

    static final class hook extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable table = arg.checktable();
            var clazz = (Class<?>) table.get("class").checkuserdata();
            var returns = table.get("returns");
            var method = table.get("method");
            var params = table.get("params");
            var replace = table.get("replace");
            var before = table.get("before");
            var after = table.get("after");

            boolean isConstructor = returns.isnil() && method.isnil();

            if (!isConstructor) {
                if (returns.isnil() || method.isnil()) {
                    throw new LuaError("Method signature not complete: expect `returns` and `method` not nil");
                }
            }

            Object[] _params = new Object[1];
            if (!params.isnil()) {
                var paramsTable = params.checktable();
                _params = new Object[paramsTable.length() + 1];
                for (int i = 0; i < paramsTable.length(); i++) {
                    _params[i] = paramsTable.get(i + 1).checkuserdata();
                }
            }

            if (!replace.isnil()) {
                _params[_params.length - 1] = new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        try {
                            LuaValue[] vargs = new LuaValue[2];
                            vargs[0] = CoerceJavaToLua.coerce(param.thisObject);
                            vargs[1] = CoerceJavaToLua.coerce(param.args);

                            Varargs ret = replace.invoke(vargs);
                            // handle return value
                            if (isConstructor || ret.narg() == 0 || ret.arg1().isnil()) return null;
                            return CoerceLuaToJava.coerce(ret.arg1(), (Class<?>) returns.checkuserdata());
                        } catch (Throwable e) {
                            Console.printErr(e);
                            throw e;
                        }
                    }
                };
            } else {
                _params[_params.length - 1] = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        call(before, param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        call(after, param);
                    }

                    private void call(LuaValue func, MethodHookParam param) {
                        try {
                            if (func.isnil()) return;

                            LuaValue[] vargs = new LuaValue[2];
                            vargs[0] = CoerceJavaToLua.coerce(param.thisObject);
                            vargs[1] = CoerceJavaToLua.coerce(param.args);

                            Varargs ret = func.invoke(vargs);
                            // handle return value
                            if (isConstructor || ret.narg() == 0) return;
                            if (ret.arg1().isnil()) {
                                param.setResult(null);
                            } else {
                                param.setResult(CoerceLuaToJava.coerce(ret.arg1(), (Class<?>) returns.checkuserdata()));
                            }
                        } catch (Throwable e) {
                            Console.printErr(e);
                        }
                    }
                };
            }

            XC_MethodHook.Unhook unhook;
            if (isConstructor) {
                unhook = XposedHelpers.findAndHookConstructor(clazz, _params);
            } else {
                unhook = XposedHelpers.findAndHookMethod(clazz, method.checkjstring(), _params);
            }

            return CoerceJavaToLua.coerce(unhook);
        }
    }
}
