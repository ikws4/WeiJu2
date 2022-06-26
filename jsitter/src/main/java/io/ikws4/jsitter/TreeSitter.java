package io.ikws4.jsitter;

import androidx.annotation.Nullable;

class TreeSitter {

    //*************************************************************************
    //* Section - Parser
    //*************************************************************************

    /**
     * Create a new parser.
     *
     * @return parser pointer
     */
    static native long parserNew();

    /**
     * Delete the parser, freeing all of the memory that it used.
     *
     * @param parserPtr parser pointer
     */
    static native void parserDelete(long parserPtr);

    /**
     * Set the language that the parser should use for parsing.
     *
     * @param parserPtr   paser pointer
     * @param languagePtr language pointer
     * @return Returns a boolean indicating wheter or not the language was
     * successfully assigned.
     */
    static native boolean parserSetLanguage(long parserPtr, long languagePtr);

    /**
     * Use the parser to parse some source code stored in one contiguous buffer (string).
     *
     * @param parserPtr  parser pointer
     * @param oldTreePtr If you are parsing this document for the first time, pass 0 (NULL).
     *                   Otherwise, if you have already parsed an earlier previous syntax tree
     *                   so that unchanged parts of it can be reused. This will save time and
     *                   memory. For this to work correctly, you must have way that exactly matches
     *                   the source code changes.
     * @param source     source code
     * @param length     source code length
     * @return tree pointer
     */
    static native long parserParseString(long parserPtr, long oldTreePtr, String source, int length);


    //*************************************************************************
    //* Section - Tree
    //*************************************************************************

    /**
     * Create a shallow copy of the syntax tree. This is very fast.
     * <p>
     * You need to copy a syntax tree in order to use it on more than
     * one thred at a time, as syntax trees are not thread safe.
     *
     * @param treePtr tree pointer
     * @return copyed tree pointer
     */
    static native long treeCopy(long treePtr);

    /**
     * Delete the syntax tree, freeing all of the memory that it used.
     *
     * @param treePtr tree pointer
     */
    static native void treeDelete(long treePtr);

    /**
     * Get the root node of the syntax tree.
     *
     * @param treePtr tree pointer
     * @return {@link TSNode}
     */
    static native TSNode treeRootNode(long treePtr);

    /**
     * Get the langauge that was used to parse the syntax tree.
     *
     * @param treePtr tree pointer
     * @return langauge pointer see {@link TSLanguages}
     */
    static native long treeLanguage(long treePtr);

    /**
     * Edit the syntax tree to keep it in sync with source code that
     * has been edited.
     * <p>
     * You mut describe the edit both in terms of byte offsets and in terms
     * of (row, column) coordinates.
     *
     * <pre>
     * typedef struct {
     *   uint32_t start_byte;
     *   uint32_t old_end_byte;
     *   uint32_t new_end_byte;
     *   TSPoint start_point;
     *   TSPoint old_end_point;
     *   TSPoint new_end_point;
     * } TSInputEdit;
     *
     * typedef struct {
     *   uint32_t row;
     *   uint32_t column;
     * } TSPoint;
     * </pre>
     *
     * @param treePtr tree pointer
     */
    static native void treeEdit(long treePtr,
                                int startByte,
                                int oldEndByte,
                                int newEndByte,
                                int startRow, int startColumn,
                                int oldEndRow, int oldEndColumn,
                                int newEndRow, int newEndColumn);


    //*************************************************************************
    //* Section - TSNode
    //*************************************************************************

    /**
     * Get the node's immediate parent.
     */
    @Nullable
    static native TSNode nodeParant(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Get the node's child at the given index, where zero represents the first
     * child.
     */
    @Nullable
    static native TSNode nodeChild(long id, long tree_ptr, int context0, int context1, int context2, int context3, int index);

    /**
     * get the node's number of children.
     */
    static native int nodeChildCount(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Get the node's <b>named</b> child at the given index, where zero represents
     * the first child.
     */
    @Nullable
    static native TSNode nodeNamedChild(long id, long tree_ptr, int context0, int context1, int context2, int context3, int index);

    /**
     * get the node's number of children.
     */
    static native int nodeNamedChildCount(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Get the node's type as a null-terminated string.
     */
    static native String nodeType(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Get the node's type as a numberial id.
     */
    static native int nodeSymbol(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Check is the node is <b>named</b> Named nodes correspond to named rules in the
     * grammar, whereas <b>anonymous</b> nodes correspond to string literals in the grammar.
     */
    static native boolean nodeIsNamed(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Check if the node is <b>missing</b>. Missing nodes are inserted by the parser in
     * order to recover from certain kinds of syntax errors.
     */
    static native boolean nodeIsMissing(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Check if the node is syntax error or contains any syntax errors.
     */
    static native boolean nodeHasError(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Get an S-expression representing the node as a string.
     */
    static native String nodeString(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Get the smallest node within this node that spans the given range of (row, column) positions.
     */
    @Nullable
    static native TSNode nodeDescendantForRange(long id, long tree_ptr, int context0, int context1, int context2, int context3, int startRow, int startColumn, int endRow, int endColumn);

    /**
     * Get the smallest named node within this node that spans the given range of (row, column) positions.
     */
    @Nullable
    static native TSNode nodeNamedDescendantForRange(long id, long tree_ptr, int context0, int context1, int context2, int context3, int startRow, int startColumn, int endRow, int endColumn);


    //*************************************************************************
    //* Section - TreeCursor
    //*************************************************************************

    /**
     * Create a new tree cursor starting from the given node.
     * <p>
     * A tree cursor allows you to walk a syntax tree more efficiently
     * than is possible using the {@link TSNode} functions. It is a mutable
     * object that is always on a certain syntax node, and can be moved
     * imperatively to different nodes.
     *
     * @return TSTreeCursor pointer
     */
    static native long treeCursorNew(long id, long tree_ptr, int context0, int context1, int context2, int context3);

    /**
     * Delete a tree cursor, freeing all of the memory that it used.
     *
     * @param treeCursorPtr TSTreeCursor pointer
     */
    static native void treeCursorDelete(long treeCursorPtr);

    /**
     * Get the tree cursor's current node.
     *
     * @param treeCursorPtr TSTreeCursor pointer
     * @return Current tree node
     */
    static native TSNode treeCursorCurrentNode(long treeCursorPtr);

    /**
     * Move the cursor to the parent of its current node.
     *
     * @param treeCursorPtr TSTreeCursor pointer
     * @return true if the cursor successfully moved,
     * false if there was no parent node.
     */
    static native boolean treeCursorGotoParent(long treeCursorPtr);

    /**
     * Move the cursor to the next sibling of its current node.
     *
     * @param treeCursorPtr TSTreeCursor pointer
     * @return true if the cursor successfully moved,
     * false if there was no the sibling node.
     */
    static native boolean treeCursorGotoNextSibling(long treeCursorPtr);

    /**
     * Move the cursor to the first child of its current ndoe.
     *
     * @param treeCursorPtr TSTreeCursor painter
     * @return true if the cursor successfully moved,
     * false if there were no children.
     */
    static native boolean treeCursorGotoFirstChild(long treeCursorPtr);


    //*************************************************************************
    //* Section - Query
    //*************************************************************************

    /**
     * Create a new query from a string containing one or more S-expression
     * patterns. The query is associated with particular langauge, and can
     * only be run on syntax nodes parsed with that langauge.
     * <p>
     * If all of the given patterns are valid, this return a {@link TSQuery} pointer.
     * If a pattern is invalid, this return 0 (NULL)
     *
     * @param languagePtr see {@link TSLanguages}
     * @param source      S-expression
     * @param length      S-expression length
     * @return TSQuery pointer
     */
    static native long queryNew(long languagePtr, String source, int length);

    /**
     * Delete a query, freeing all of the memory that is used.
     *
     * @param queryPtr TSQuery pointer
     */
    static native void queryDelete(long queryPtr);

    /**
     * Create a new cursor for executing a given query.
     * <p>
     * The cursor stores the state that is needed to iteratively search
     * for matches. To use the query cursor, first call {@link #queryCursorExec(long, long, long, long, int, int, int, int)}
     * to start running a given query on a given syntax node.
     *
     * @return TSQueryCursor pointer
     */
    static native long queryCursorNew();

    /**
     * Delete a query cursor, freeing all of the memory that is used.
     *
     * @param queryCursorPtr TSQueryCursor pointer
     */
    static native void queryCursorDelete(long queryCursorPtr);

    /**
     * Start running a give query on a given {@link TSNode}.
     *
     * @param queryCursorPtr TSQueryCursor pointer
     * @param queryPtr TSQuery pointer
     */
    static native void queryCursorExec(long queryCursorPtr, long queryPtr, long id, long treePtr, int context0, int context1, int context2, int context3);

    /**
     * Set the range of bytes in whitch the query will be executed.
     * @param queryCursorPtr TSQueryCursor pointer
     */
    static native void queryCursorSetByteRange(long queryCursorPtr, int startByte, int endByte);

    /**
     * Set the range of (row, column) in whitch the query will be executed.
     * @param queryCursorPtr TSQueryCursor pointer
     */
    static native void queryCursorSetPointRange(long queryCursorPtr, int startRow, int startColumn, int endRow, int endColumn);

    /**
     * Adcance to the next match of the currently running query.
     *
     * @param queryCursorPtr TSQueryCursor pointer
     * @param queryPtr TSQuery pointer
     * @return {@link TSQueryMatch} if there is a match. Otherwise, return null.
     */
    @Nullable
    static native TSQueryMatch queryCursorNextMatch(long queryCursorPtr, long queryPtr);
}