package io.ikws4.weiju.util;

import java.util.Arrays;
import java.util.List;

public class Strings {
    private static final StringBuilder sb = new StringBuilder();

    public static String repeat(CharSequence str, int n) {
        sb.setLength(0);
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String join(CharSequence delimiter, CharSequence... strs) {
        return join(delimiter, Arrays.asList(strs));
    }

    public static String join(CharSequence delimiter, List<CharSequence> strs) {
        sb.setLength(0);
        for (int i = 0; i < strs.size(); i++) {
            sb.append(strs.get(i));
            if (i < strs.size() - 1) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }
}
