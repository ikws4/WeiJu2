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

    public static int leadingSpaceCount(CharSequence str) {
        int i = 0;
        while (i < str.length() && str.charAt(i) == ' ') i++;
        return i;
    }

    public static boolean isOnlySpaces(CharSequence s) {
        if (s.length() == 0) return false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ') return false;
        }
        return true;
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

    public static String reindentMutipleLine(String s) {
        int leadingSpace = 0;
        int n = s.length();
        for (int i = 0; i < n; i++) {
            if (s.charAt(i) == ' ') leadingSpace++;
            else break;
        }

        sb.setLength(0);
        int i = 0;
        while (i < n) {
            i += leadingSpace;
            while (i < n && s.charAt(i) != '\n') {
                sb.append(s.charAt(i));
                i++;
            }
            if (i < n) {
                sb.append('\n');
                i++; // skip newline
            }
        }

        return sb.toString();
    }
}
