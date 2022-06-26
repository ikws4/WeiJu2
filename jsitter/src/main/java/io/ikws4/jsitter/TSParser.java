package io.ikws4.jsitter;

public class TSParser implements AutoCloseable {
    private final long ptr;
    private long language;

    public TSParser() {
        this.ptr = TreeSitter.parserNew();
    }

    public TSParser(long language) {
        this();
        setLanguage(language);
    }

    public void setLanguage(long language) {
        this.language = language;
        TreeSitter.parserSetLanguage(ptr, language);
    }

    public long getLanguage() {
        return language;
    }

    public TSTree parse(String source, TSTree oldTree) {
        long oldTreePtr = oldTree == null ? 0 : oldTree.ptr;
        return new TSTree(TreeSitter.parserParseString(ptr, oldTreePtr, source, source.length()));
    }

    @Override
    public void close() {
        TreeSitter.parserDelete(ptr);
    }
}