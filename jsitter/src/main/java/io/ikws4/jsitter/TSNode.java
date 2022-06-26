package io.ikws4.jsitter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TSNode {
    long id;
    long treePtr;
    int context0;
    int context1;
    int context2;
    int context3;
    private int endByte;
    private int endRow;
    private int endColumn;

    public int getStartByte() {
        return context0;
    }

    public int getEndByte() {
        return endByte;
    }

    public int getStartRow() {
        return context1;
    }

    public int getStartColumn() {
        return context2;
    }

    public int getEndRow() {
        return endRow;
    }

    public int getEndColumn() {
        return endColumn;
    }

    @Nullable
    public TSNode getParent() {
        return TreeSitter.nodeParant(id, treePtr, context0, context1, context2, context3);
    }

    @Nullable
    public TSNode getChild(int index) {
        return TreeSitter.nodeChild(id, treePtr, context0, context1, context2, context3, index);
    }

    public int getChildCount() {
        return TreeSitter.nodeChildCount(id, treePtr, context0, context1, context2, context3);
    }

    @Nullable
    public TSNode getNamedChild(int index) {
        return TreeSitter.nodeNamedChild(id, treePtr, context0, context1, context2, context3, index);
    }

    public int getNamedChildCount() {
        return TreeSitter.nodeNamedChildCount(id, treePtr, context0, context1, context2, context3);
    }

    public String getType() {
        return TreeSitter.nodeType(id, treePtr, context0, context1, context2, context3);
    }

    public int getSymbol() {
        return TreeSitter.nodeSymbol(id, treePtr, context0, context1, context2, context3);
    }

    public boolean isNamed() {
        return TreeSitter.nodeIsNamed(id, treePtr, context0, context1, context2, context3);
    }

    public boolean isMissing() {
        return TreeSitter.nodeIsMissing(id, treePtr, context0, context1, context2, context3);
    }

    public boolean hasError() {
        return TreeSitter.nodeHasError(id, treePtr, context0, context1, context2, context3);
    }

    public boolean hasChildren() {
        return getChildCount() > 0;
    }

    public String getSExpr() {
        return TreeSitter.nodeString(id, treePtr, context0, context1, context2, context3);
    }

    @Nullable
    public TSNode decendantForRange(int startRow, int startColumn, int endRow, int endColumn) {
        return TreeSitter.nodeDescendantForRange(id, treePtr, context0, context1, context2, context3, startRow, startColumn, endRow, endColumn);
    }

    @Nullable
    public TSNode namedDecendantForRange(int startRow, int startColumn, int endRow, int endColumn) {
        return TreeSitter.nodeNamedDescendantForRange(id, treePtr, context0, context1, context2, context3, startRow, startColumn, endRow, endColumn);
    }

    public Iterable<TSNode> childrenIter() {
        return new Iterable<TSNode>() {
            @NonNull
            @Override
            public Iterator<TSNode> iterator() {
                return new Iterator<TSNode>() {
                    private int index = 0;
                    private final int size = getChildCount();

                    @Override
                    public boolean hasNext() {
                        return index < size;
                    }

                    @Override
                    public TSNode next() {
                        return getChild(index++);
                    }
                };
            }
        };
    }

    public TSTreeCursor cursor() {
        return new TSTreeCursor(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TSNode tsNode = (TSNode) o;
        return id == tsNode.id &&
                treePtr == tsNode.treePtr;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, treePtr);
    }

    @Override
    public String toString() {
        return "TSNode{" +
                "startByte=" + context0 +
                ", endByte=" + endByte +
                ", startRow=" + context1 +
                ", endRow=" + endRow +
                ", startColumn=" + context2 +
                ", endColumn=" + endColumn +
                ", type='" + getType() + '\'' +
                '}';
    }
}