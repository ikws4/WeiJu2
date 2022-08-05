package io.ikws4.weiju.xposed;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.ikws4.weiju.util.Logger;

/**
 * All the packages are in <i>app/src/main/resources/</i> folder
 */
public class BuiltinPackage {
    private static final Map<String, String> scripts = new HashMap<>();

    public static String require(String path) {
        if (scripts.containsKey(path)) return scripts.get(path);

        String script = "";
        try {
            InputStream in = BuiltinPackage.class.getClassLoader().getResourceAsStream(path + ".lua");

            if (in == null) {
                throw new IOException("package: '" + path + "' not found");
            }
            byte[] bytes = new byte[in.available()];
            in.read(bytes);

            script = new String(bytes);
        } catch (IOException e) {
            Logger.e(e);
        }

        scripts.put(path, script);
        return script;
    }
}
