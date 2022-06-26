package io.ikws4.codeeditor.api.language;

import androidx.annotation.NonNull;

public class Suggestion {
    private final Type mType;
    private final String mText;
    private final String mReturnType;

    public Suggestion(Type type, String text) {
        this(type, text, "");
    }

    public Suggestion(Type type, String text, String returnType) {
        mType = type;
        mText = text;
        mReturnType = returnType;
    }

    public Type getType() {
        return mType;
    }

    public String getText() {
        return mText;
    }

    public String getReturnType() {
        return mReturnType;
    }

    @NonNull
    @Override
    public String toString() {
        return mText;
    }

    public enum Type {
        KEYWORD("k"),

        IDENTIFIER("w"),

        FUNCTION("f"),

        FIELD("v"),

        TYPE("t");

        private final String mText;

        Type(String text) {
            mText = text;
        }

        @NonNull
        @Override
        public String toString() {
            return mText;
        }
    }
}
