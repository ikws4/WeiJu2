package io.ikws4.codeeditor.configuration.indent;

import androidx.annotation.NonNull;

enum Whitespace {

    /**
     * A single space character.
     */
    SPACE(" "),

    /**
     * A single tab character.
     */
    TAB("\t");

    private final String mWhitespaceString;

    Whitespace(String whitespaceString) {
        mWhitespaceString = whitespaceString;
    }

    @NonNull
    @Override
    public String toString() {
        return mWhitespaceString;
    }
}
