package io.ikws4.weiju.xposed;

import com.android.dx.stock.ProxyBuilder;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.LuajavaLib;

import de.robv.android.xposed.XposedHelpers;

public class XposedLuajavaLib extends LuajavaLib {

    @Override
    protected Object object(Class clazz, LuaValue lobj) {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(lobj);

        try {
            return ProxyBuilder.forClass(clazz)
                .dexCache(XposedInit.context.get().getCodeCacheDir())
                .parentClassLoader(XposedInit.lpparam.classLoader)
                .handler(handler)
                .build();
        } catch (Exception e) {
            throw new LuaError(e);
        }
    }

    @Override
    protected Class classForName(String name) {
        try {
            return XposedHelpers.findClass(name, XposedInit.lpparam.classLoader);
        } catch (XposedHelpers.ClassNotFoundError e) {
            throw new LuaError(e);
        }
    }
}
