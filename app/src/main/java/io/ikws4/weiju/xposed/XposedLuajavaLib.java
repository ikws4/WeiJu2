package io.ikws4.weiju.xposed;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.LuajavaLib;

import de.robv.android.xposed.XposedHelpers;

public class XposedLuajavaLib extends LuajavaLib {

    @Override
    protected Class classForName(String name) {
        try {
            return XposedHelpers.findClass(name, XposedInit.classloader);
        } catch (XposedHelpers.ClassNotFoundError e) {
            throw new LuaError(e);
        }
    }
}
