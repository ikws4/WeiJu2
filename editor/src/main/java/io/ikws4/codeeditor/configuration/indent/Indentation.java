package io.ikws4.codeeditor.configuration.indent;

public class Indentation {

    public static final Indentation TAB = new Indentation(Whitespace.TAB.toString(), 1);

    public static final Indentation WHITE_SPACE_2 = new Indentation(Whitespace.SPACE.toString(), 2);

    public static final Indentation WHITE_SPACE_4 = new Indentation(Whitespace.SPACE.toString(), 4);

    public static final Indentation WHITE_SPACE_8 = new Indentation(Whitespace.SPACE.toString(), 8);

    private final String mIndentationString;

    private Indentation(String indentationString, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(indentationString);
        }
        mIndentationString = builder.toString();
    }

    /**
     * Returns the string that needs to be appended at the beginning of a line,
     * with the result that the line is indented by the given level.
     *
     * @param level The identation level.
     *
     * @return The indentation string.
     */
    public String get(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append(mIndentationString);
        }
        return builder.toString();
    }
}

