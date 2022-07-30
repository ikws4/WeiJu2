package io.ikws4.weiju.lua;

import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;
import org.luaj.vm2.parser.Token;
import org.luaj.vm2.parser.TokenMgrError;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Consumer;


public class LuaDiagnostic {
    public static void diagnose(String source, Consumer<DiagnosticInfo> callback) {
        InputStream in = new ByteArrayInputStream(source.getBytes());
        LuaParser parser = new LuaParser(in);

        try {
            parser.Chunk();
        } catch (ParseException e) {
            Token t = e.currentToken;
            if (t != null && t.beginLine > 0 && t.beginColumn > 0 && t.endLine > 0 && t.endColumn > 0) {
                callback.accept(DiagnosticInfo.obtain(e.getMessage(), t.beginLine - 1, t.beginColumn - 1, t.endLine - 1, t.endColumn - 1));
            }
        } catch (TokenMgrError ignored) {
        }
    }
}
