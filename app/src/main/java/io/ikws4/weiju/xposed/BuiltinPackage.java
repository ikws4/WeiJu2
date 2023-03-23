package io.ikws4.weiju.xposed;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * All the packages are in <i>app/src/main/resources/</i> folder
 */
class BuiltinPackage {
    private static final Map<String, String> scripts = new HashMap<>();

    /**
     * returns an empty string if script not exist.
     */
    public static String require(String path) {
        if (scripts.containsKey(path)) return scripts.get(path);

        try (InputStream in = BuiltinPackage.class.getClassLoader().getResourceAsStream(path + ".lua")) {

            if (in == null) {
                throw new IOException("package: '" + path + "' not found");
            }
            byte[] bytes = new byte[in.available()];
            in.read(bytes);

            String script = new String(bytes);
            scripts.put(path, script);

            return script;
        } catch (IOException e) {
            return "";
        }
    }
}
