package io.ikws4.jsitter;

public class TSTree implements AutoCloseable {
    final long ptr;

    TSTree(long ptr) {
        this.ptr = ptr;
    }

    public void edit(int startByte, int oldEndByte, int newEndByte, int startRow, int startColumn, int oldEndRow, int oldEndColumn, int newEndRow, int newEndColumn) {
        TreeSitter.treeEdit(ptr,
                startByte,
                oldEndByte,
                newEndByte,
                startRow, startColumn,
                oldEndRow, oldEndColumn,
                newEndRow, newEndColumn);
    }

    public TSNode getRoot() {
        return TreeSitter.treeRootNode(ptr);
    }

    public long getLanguage() {
        return TreeSitter.treeLanguage(ptr);
    }

    public TSTree copy() {
        return new TSTree(TreeSitter.treeCopy(ptr));
    }

    @Override
    public void close() {
        TreeSitter.treeDelete(ptr);
    }
}