package io.ikws4.weiju.xposed;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.JseMathLib;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class XposedPlatform {
    private static final PrintStream STDOUT = new PrintStream(new ByteArrayOutputStream() {
        @Override
        public void flush() {
            Console.printMsg(new String(toByteArray()));
        }
    }, true);

    private static final PrintStream STDERR = new PrintStream(new ByteArrayOutputStream() {
        @Override
        public void flush() {
            Console.printErr(new String(toByteArray()));
        }
    }, true);

    public static Globals create() {
        Globals globals = new Globals();

        // java impl libs
        globals.load(new XposedBaselib());
        globals.load(new XposedPackageLib());
        globals.load(new Bit32Lib());
        globals.load(new TableLib());
        globals.load(new StringLib());
        globals.load(new CoroutineLib());
        globals.load(new JseMathLib());
        globals.load(new XposedLuajavaLib());

        // redirect output stream to console
        globals.STDOUT = STDOUT;
        globals.STDERR = STDERR;

        // init compiler
        LoadState.install(globals);
        LuaC.install(globals);

        // lua libs
        globals.load(BuiltinPackage.require("java_common_syntax")).call();
        globals.load(BuiltinPackage.require("java_primitives")).call();
        globals.load(BuiltinPackage.require("table_util")).call();

        return globals;
    }
}
