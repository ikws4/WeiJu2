package io.ikws4.jsitter;

import androidx.annotation.NonNull;

import java.util.Iterator;

public class TSQuery implements AutoCloseable {
    private final long ptr;
    private final long queryCursorPtr;

    /**
     * Create a new query from a string containing one or more S-expression
     * patterns. The query is associated with a particular language, and can
     * only be run on syntax nodes parsed with that language.
     *
     * @param language {@link TSLanguages}
     * @param source   S-expression
     */
    public TSQuery(long language, String source) {
        ptr = TreeSitter.queryNew(language, source, source.length());
        queryCursorPtr = TreeSitter.queryCursorNew();
    }

    public Iterable<TSQueryCapture> captureIter(TSNode node) {
        return captureIter(node, 0, 0);
    }

    public Iterable<TSQueryCapture> captureIter(TSNode node, int startRow, int endRow) {
        final Iterator<TSQueryMatch> matchIterator = matchIter(node, startRow, endRow).iterator();

        return new Iterable<TSQueryCapture>() {
            @NonNull
            @Override
            public Iterator<TSQueryCapture> iterator() {
                return new Iterator<TSQueryCapture>() {
                    private TSQueryMatch match = matchIterator.next();
                    private int index;

                    @Override
                    public boolean hasNext() {
                        return match != null && index < match.captureCount();
                    }

                    @Override
                    public TSQueryCapture next() {
                        TSQueryCapture capture = match.getCapture(index++);
                        if (index >= match.captureCount()) {
                            match = matchIterator.next();
                            index = 0;
                        }
                        return capture;
                    }
                };
            }
        };
    }

    public Iterable<TSQueryMatch> matchIter(TSNode node) {
        return matchIter(node, 0, 0);
    }

    public Iterable<TSQueryMatch> matchIter(TSNode node, int startRow, int endRow) {
        TreeSitter.queryCursorSetPointRange(queryCursorPtr, startRow, 0, endRow, -0);
        TreeSitter.queryCursorExec(queryCursorPtr, ptr, node.id, node.treePtr, node.context0, node.context1, node.context2, node.context3);

        return new Iterable<TSQueryMatch>() {
            @NonNull
            @Override
            public Iterator<TSQueryMatch> iterator() {
                return new Iterator<TSQueryMatch>() {
                    private TSQueryMatch match = nextMatch();

                    @Override
                    public boolean hasNext() {
                        return match != null;
                    }

                    @Override
                    public TSQueryMatch next() {
                        TSQueryMatch m = match;
                        match = nextMatch();
                        return m;
                    }
                };
            }
        };
    }

    private TSQueryMatch nextMatch() {
        return TreeSitter.queryCursorNextMatch(queryCursorPtr, ptr);
    }

    @Override
    public void close() {
        TreeSitter.queryDelete(ptr);
        TreeSitter.queryCursorDelete(queryCursorPtr);
    }
}
