package io.ikws4.jsitter;

public class TSTreeCursor implements AutoCloseable {
    private final long ptr;
    private long id;
    private long tree;
    private int context0;
    private int context1;

    TSTreeCursor(TSNode node) {
        this.ptr = TreeSitter.treeCursorNew(node.id, node.treePtr, node.context0, node.context1, node.context2, node.context3);
    }

    public TSNode getCurrentNode() {
        return TreeSitter.treeCursorCurrentNode(ptr);
    }

    public boolean gotoFirstChild() {
        return TreeSitter.treeCursorGotoFirstChild(ptr);
    }

    public boolean gotoNextSibling() {
        return TreeSitter.treeCursorGotoNextSibling(ptr);
    }

    public boolean gotoParent() {
        return TreeSitter.treeCursorGotoParent(ptr);
    }

    @Override
    public void close() {
        TreeSitter.treeCursorDelete(ptr);
    }
}