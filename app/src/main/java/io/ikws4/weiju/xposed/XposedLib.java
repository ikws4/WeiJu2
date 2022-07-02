package io.ikws4.weiju.xposed;

import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

class XposedLib extends TwoArgFunction {
  @Override
  public LuaValue call(LuaValue modname, LuaValue env) {
    LuaTable xposed = new LuaTable();
    xposed.set("log", new x_log());
    xposed.set("find_and_hook_method", new x_find_and_hook_method());

    env.set("xposed", xposed);
    env.get("package").get("loaded").set("xposed", xposed);
    return env;
  }

  static final class x_log extends OneArgFunction {

    @Override
    public LuaValue call(LuaValue arg) {
      XposedBridge.log(arg.checkjstring());
      return LuaValue.NIL;
    }
  }

  static final class x_find_and_hook_method extends VarArgFunction {
    @Override
    public Varargs invoke(Varargs args) {
      int n = args.narg();
      Class<?> clazz = (Class<?>) args.arg(1).checkuserdata();
      String methodName = args.arg(2).checkjstring();
      Object[] params = new Object[n - 2];
      XC_MethodHook.Unhook res = XposedHelpers.findAndHookMethod(clazz, methodName, params);
      return varargsOf(LuaValue.userdataOf(res), NIL);
    }
  }
}
