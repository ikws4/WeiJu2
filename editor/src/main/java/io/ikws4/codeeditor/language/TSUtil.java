package io.ikws4.codeeditor.language;

import io.ikws4.jsitter.TSNode;

class TSUtil {
    public static TSNode getNodeAtLine(TSNode root, int line) {
        for (TSNode node : root.childrenIter()) {
            int startRow = node.getStartRow();
            int endRow = node.getEndRow();
            if (startRow == line) return node;

            if (node.getChildCount() > 0 && startRow < line && line <= endRow) {
                return getNodeAtLine(node, line);
            }
        }
        return null;
    }
}
