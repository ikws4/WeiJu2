import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class java_primitive_types extends TwoArgFunction {

    public java_primitive_types() {}

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = LuaValue.tableOf();
        env.set("byte", LuaValue.userdataOf(byte.class));
        env.set("short", LuaValue.userdataOf(short.class));
        env.set("int", LuaValue.userdataOf(int.class));
        env.set("long", LuaValue.userdataOf(long.class));
        env.set("float", LuaValue.userdataOf(float.class));
        env.set("double", LuaValue.userdataOf(double.class));
        env.set("char", LuaValue.userdataOf(char.class));
        env.set("boolean", LuaValue.userdataOf(boolean.class));
        env.set("void", LuaValue.userdataOf(void.class));
        env.set("java_primitive_types", library);
        return library;
    }
}
