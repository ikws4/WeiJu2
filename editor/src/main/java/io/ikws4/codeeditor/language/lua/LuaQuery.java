package io.ikws4.codeeditor.language.lua;

import io.ikws4.codeeditor.language.TSLangaugeQuery;

class LuaQuery implements TSLangaugeQuery {
    @Override
    public String highlight() {
        return ScmsKt.HIGHTLIGHT;
    }

    @Override
    public String indent() {
        return ScmsKt.INDENT;
    }
}
