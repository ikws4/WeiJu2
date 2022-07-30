package io.ikws4.weiju.lua;

import java.util.ArrayDeque;
import java.util.Deque;

public class DiagnosticInfo {
    private static final Deque<DiagnosticInfo> sPool = new ArrayDeque<>(256);

    public String msg;
    public int startLine, startColumn;
    public int endLine, endColumn;

    private DiagnosticInfo(String msg, int startLine, int startColumn, int endLine, int endColumn) {
        this.msg = msg;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public static DiagnosticInfo obtain(String msg, int startLine, int startColumn, int endLine, int endColumn) {
        DiagnosticInfo info = sPool.poll();
        if (info == null) {
            info = new DiagnosticInfo(msg, startLine, startColumn, endLine, endColumn);
        }
        info.msg = msg;
        return info;
    }

    public void recycle() {
        sPool.offer(this);
    }
}
