package io.ikws4.weiju.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple template engine.
 * Can parse <b>${name}</b> as placeholde,
 * and use {@link Template#set(String, String)} to
 * set the value.
 */
public class Template {
    private final String mSource;
    private final Map<String, Span> mKeywordSpan = new HashMap<>();

    public Template(InputStream in) {
        String source = "";
        try {
            byte[] bytes = new byte[in.available()];
            int i = 0;
            int c;
            while ((c = in.read()) != -1) {
                bytes[i] = (byte) c;
                i++;
            }
            source = new String(bytes, 0, i);
        } catch (IOException e) {
            Logger.d(e);
        }
        mSource = source;
    }

    public Template(String source) {
        mSource = source;
        parse();
    }

    private void parse() {
        int i = 0;
        int n = mSource.length();
        while (i < mSource.length()) {
            if (mSource.charAt(i) == '$') {
                i++;
                if (i < n && mSource.charAt(i) == '{') {
                    i++;

                    int start = i;
                    int end = i;
                    while (end < n && mSource.charAt(end) != '}') {
                        end++;
                    }

                    String keyword = mSource.substring(start, end);
                    // "${longgggggggggggname}"
                    //    ^                  ^
                    //  start               end
                    mKeywordSpan.put(keyword, new Span(start - 2, end, ""));

                    if (end == mSource.length()) {
                        throw new IllegalArgumentException("Expect a '}'");
                    }
                }
            }

            i++;
        }
    }

    public void set(String name, String value) {
        Span span = mKeywordSpan.get(name);
        if (span == null) {
            throw new IllegalArgumentException("Can't find a placeholder with name '" + name + "'");
        }
        span.value = value;
    }

    @Override
    public String toString() {
        List<Span> spans = new ArrayList<>(mKeywordSpan.values());
        Collections.sort(spans);

        int i = 0;
        int n = mSource.length();
        StringBuilder sb = new StringBuilder(mSource.length());
        for (Span span : spans) {
            while (i < span.start) {
                sb.append(mSource.charAt(i));
                i++;
            }
            sb.append(span.value);
            i = span.end + 1;
        }
        while (i < n) {
            sb.append(mSource.charAt(i));
            i++;
        }

        return sb.toString();
    }

    private static class Span implements Comparable<Span> {
        final int start, end;
        String value;

        private Span(int start, int end, String value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }

        @Override
        public int compareTo(Span o) {
            int res = Integer.compare(start, o.start);
            if (res == 0) return Integer.compare(end, o.end);
            return res;
        }
    }
}
