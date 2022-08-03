package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

class XposedLib extends TwoArgFunction {
    private final XC_LoadPackage.LoadPackageParam lpparam;

    XposedLib(XC_LoadPackage.LoadPackageParam lpparam) {
        this.lpparam = lpparam;
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable xp = new LuaTable();
        xp.set("hook", new hook());
        xp.set("set_field", new set_field());
        xp.set("get_field", new get_field());
        xp.set("new", new _new());
        xp.set("call", new _call());
        xp.set("lpparam", CoerceJavaToLua.coerce(lpparam));

        env.set("xp", xp);
        env.get("package").get("loaded").set("xp", xp);
        return env;
    }

    static final class hook extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable table = arg.checktable();
            var clazz = table.get("class").checkjstring();
            var returns = table.get("returns").checkjstring();
            var method = table.get("method");
            var params = table.get("params");
            var replace = table.get("replace");
            var before = table.get("before");
            var after = table.get("after");

            Object[] _params = new Object[1];
            if (!params.isnil()) {
                var paramsTable = params.checktable();
                _params = new Object[paramsTable.length() + 1];
                for (int i = 0; i < paramsTable.length(); i++) {
                    _params[i] = paramsTable.get(i + 1).checkjstring();
                }
            }

            if (!replace.isnil()) {
                _params[_params.length - 1] = new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        LuaValue[] vargs = new LuaValue[2];
                        vargs[0] = CoerceJavaToLua.coerce(param.thisObject);
                        vargs[1] = CoerceJavaToLua.coerce(param.args);

                        Varargs ret = replace.invoke(vargs);
                        // handle return value
                        if (ret.narg() == 0 || ret.arg1().isnil()) return null;
                        return CoerceLuaToJava.coerce(ret.arg1(), XposedHelpers.findClass(returns, XposedInit.classLoader));
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
                        if (func.isnil()) return;

                        LuaValue[] vargs = new LuaValue[2];
                        vargs[0] = CoerceJavaToLua.coerce(param.thisObject);
                        vargs[1] = CoerceJavaToLua.coerce(param.args);

                        Varargs ret = func.invoke(vargs);
                        // handle return value
                        if (ret.narg() == 0) return;
                        if (ret.arg1().isnil()) {
                            param.setResult(null);
                        } else {
                            param.setResult(CoerceLuaToJava.coerce(ret.arg1(), XposedHelpers.findClass(returns, XposedInit.classLoader)));
                        }
                    }
                };
            }

            XC_MethodHook.Unhook unhook;
            if (method.isnil()) {
                unhook = XposedHelpers.findAndHookConstructor(clazz, XposedInit.classLoader, _params);
            } else {
                unhook = XposedHelpers.findAndHookMethod(clazz, XposedInit.classLoader, method.checkjstring(), _params);
            }

            return CoerceJavaToLua.coerce(unhook);
        }
    }

    static final class set_field extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable table = arg.checktable();
            var clazz = table.get("class");
            var object = table.get("object");
            var type = table.get("type").checkjstring();
            var field = table.get("field").checkjstring();
            var value = table.get("value");

            if (!object.isnil()) {
                Object o = object.checkuserdata();
                switch (type) {
                    case "boolean":
                        XposedHelpers.setBooleanField(o, field, value.checkboolean());
                        break;
                    case "char":
                        XposedHelpers.setCharField(o, field, value.checkjstring().charAt(0));
                        break;
                    case "byte":
                        XposedHelpers.setByteField(o, field, (byte) value.checkint());
                        break;
                    case "short":
                        XposedHelpers.setShortField(o, field, (short) value.checkint());
                        break;
                    case "int":
                        XposedHelpers.setIntField(o, field, value.checkint());
                        break;
                    case "long":
                        XposedHelpers.setLongField(o, field, value.checklong());
                        break;
                    case "float":
                        XposedHelpers.setFloatField(o, field, (float) value.checkdouble());
                        break;
                    case "double":
                        XposedHelpers.setDoubleField(o, field, value.checkdouble());
                        break;
                    case "java.lang.String":
                        XposedHelpers.setObjectField(o, field, value.checkjstring());
                        break;
                    default:
                        if (value.isnil()) {
                            XposedHelpers.setObjectField(o, field, null);
                        } else {
                            XposedHelpers.setObjectField(o, field, value.checkuserdata());
                        }
                }
            } else if (!clazz.isnil()) {
                var klass = XposedHelpers.findClass(clazz.checkjstring(), XposedInit.classLoader);
                switch (type) {
                    case "boolean":
                        XposedHelpers.setStaticBooleanField(klass, field, value.checkboolean());
                        break;
                    case "char":
                        XposedHelpers.setStaticCharField(klass, field, value.checkjstring().charAt(0));
                        break;
                    case "byte":
                        XposedHelpers.setStaticByteField(klass, field, (byte) value.checkint());
                        break;
                    case "short":
                        XposedHelpers.setStaticShortField(klass, field, (short) value.checkint());
                        break;
                    case "int":
                        XposedHelpers.setStaticIntField(klass, field, value.checkint());
                        break;
                    case "long":
                        XposedHelpers.setStaticLongField(klass, field, value.checklong());
                        break;
                    case "float":
                        XposedHelpers.setStaticFloatField(klass, field, (float) value.checkdouble());
                        break;
                    case "double":
                        XposedHelpers.setStaticDoubleField(klass, field, value.checkdouble());
                        break;
                    case "java.lang.String":
                        XposedHelpers.setStaticObjectField(klass, field, value.checkjstring());
                        break;
                    default:
                        if (value.isnil()) {
                            XposedHelpers.setStaticObjectField(klass, field, null);
                        } else {
                            XposedHelpers.setStaticObjectField(klass, field, value.checkuserdata());
                        }
                }
            } else {
                throw new LuaError("Expecte: 'class' or 'object'");
            }

            return NIL;
        }
    }

    static final class get_field extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable table = arg.checktable();
            var clazz = table.get("class");
            var object = table.get("object");
            var type = table.get("type").checkjstring();
            var field = table.get("field").checkjstring();

            if (!object.isnil()) {
                Object o = object.checkuserdata();
                switch (type) {
                    case "boolean":
                        return CoerceJavaToLua.coerce(XposedHelpers.getBooleanField(o, field));
                    case "char":
                        return CoerceJavaToLua.coerce(XposedHelpers.getCharField(o, field));
                    case "byte":
                        return CoerceJavaToLua.coerce(XposedHelpers.getByteField(o, field));
                    case "short":
                        return CoerceJavaToLua.coerce(XposedHelpers.getShortField(o, field));
                    case "int":
                        return CoerceJavaToLua.coerce(XposedHelpers.getIntField(o, field));
                    case "long":
                        return CoerceJavaToLua.coerce(XposedHelpers.getLongField(o, field));
                    case "float":
                        return CoerceJavaToLua.coerce(XposedHelpers.getFloatField(o, field));
                    case "double":
                        return CoerceJavaToLua.coerce(XposedHelpers.getDoubleField(o, field));
                    default:
                        return CoerceJavaToLua.coerce(XposedHelpers.getObjectField(o, field));
                }
            } else if (!clazz.isnil()) {
                var klass = XposedHelpers.findClass(clazz.checkjstring(), XposedInit.classLoader);
                switch (type) {
                    case "boolean":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticBooleanField(klass, field));
                    case "char":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticCharField(klass, field));
                    case "byte":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticByteField(klass, field));
                    case "short":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticShortField(klass, field));
                    case "int":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticIntField(klass, field));
                    case "long":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticLongField(klass, field));
                    case "float":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticFloatField(klass, field));
                    case "double":
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticDoubleField(klass, field));
                    default:
                        return CoerceJavaToLua.coerce(XposedHelpers.getStaticObjectField(klass, field));
                }
            } else {
                throw new LuaError("Expecte: 'class' or 'object'");
            }
        }
    }

    static final class _new extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable table = arg.checktable();
            var clazz = table.get("class").checkjstring();
            var params = table.get("params");

            var klass = XposedHelpers.findClass(clazz, XposedInit.classLoader);
            if (params.isnil()) {
                return CoerceJavaToLua.coerce(XposedHelpers.newInstance(klass));
            } else {
                var _params = params.checktable();
                var values = new Object[255];
                var kv = _params.next(NIL);
                int i = 0;
                while (kv != NIL) {
                    var type = XposedHelpers.findClass(kv.arg(1).checkjstring(), XposedInit.classLoader);
                    values[i++] = CoerceLuaToJava.coerce(kv.arg(2), type);
                    kv = _params.next(kv.arg1());
                }
                return CoerceJavaToLua.coerce(XposedHelpers.newInstance(klass, Arrays.copyOf(values, i)));
            }
        }
    }

    static final class _call extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue arg) {
            LuaTable table = arg.checktable();
            var clazz = table.get("class");
            var object = table.get("object");
            var method = table.get("method").checkjstring();
            var params = table.get("params");

            if (!object.isnil()) {
                Object o = object.checkuserdata();
                if (params.isnil()) {
                    return CoerceJavaToLua.coerce(XposedHelpers.callMethod(o, method));
                } else {
                    var _params = params.checktable();
                    var values = new Object[255];
                    var kv = _params.next(NIL);
                    int i = 0;
                    while (kv != NIL) {
                        var type = XposedHelpers.findClass(kv.arg(1).checkjstring(), XposedInit.classLoader);
                        values[i++] = CoerceLuaToJava.coerce(kv.arg(2), type);
                        kv = _params.next(kv.arg1());
                    }
                    return CoerceJavaToLua.coerce(XposedHelpers.callMethod(o, method, Arrays.copyOf(values, i)));
                }

            } else if (!clazz.isnil()) {
                var klass = XposedHelpers.findClass(clazz.checkjstring(), XposedInit.classLoader);
                if (params.isnil()) {
                    return CoerceJavaToLua.coerce(XposedHelpers.callStaticMethod(klass, method));
                } else {
                    var _params = params.checktable();
                    var values = new Object[255];
                    var kv = _params.next(NIL);
                    int i = 0;
                    while (kv != NIL) {
                        var type = XposedHelpers.findClass(kv.arg(1).checkjstring(), XposedInit.classLoader);
                        values[i++] = CoerceLuaToJava.coerce(kv.arg(2), type);
                        kv = _params.next(kv.arg1());
                    }
                    return CoerceJavaToLua.coerce(XposedHelpers.callStaticMethod(klass, method, Arrays.copyOf(values, i)));
                }

            } else {
                throw new LuaError("Expecte: 'class' or 'object'");
            }
        }
    }
}
