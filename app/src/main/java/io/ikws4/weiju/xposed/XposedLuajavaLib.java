package io.ikws4.weiju.xposed;

import org.luaj.vm2.lib.jse.LuajavaLib;

import de.robv.android.xposed.XposedHelpers;

public class XposedLuajavaLib extends LuajavaLib {

    @Override
    protected Class classForName(String name) {
        return XposedHelpers.findClass(name, XposedInit.classLoader);
    }
}
