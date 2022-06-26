package io.ikws4.codeeditor.language.java;

import io.ikws4.codeeditor.language.TSLangaugeQuery;

class JavaQuery implements TSLangaugeQuery {
    @Override
    public String highlight() {
        return ScmsKt.HIGHTLIGHT;
    }

    @Override
    public String indent() {
        return ScmsKt.INDENT;
    }
}
