package io.ikws4.weiju.editor;

class RosepineColorScheme extends TSEditorColorScheme {
    public static final int BASE = 0xff232136;
    public static final int SURFACE = 0xff2a273f;
    public static final int OVERLAY = 0xff393552;
    public static final int MUTED = 0xff6e6a86;
    public static final int SUBTLE = 0xff908caa;
    public static final int TEXT = 0xffe0def4;
    public static final int LOVE = 0xffeb6f92;
    public static final int GOLD = 0xfff6c177;
    public static final int ROSE = 0xffea9a97;
    public static final int PINE = 0xff3e8fb0;
    public static final int FOAM = 0xff9ccfd8;
    public static final int IRIS = 0xffc4a7e7;
    public static final int HIGHLIGHT_LOW = 0xff2a283e;
    public static final int HIGHLIGHT_MED = 0xff44415a;
    public static final int HIGHLIGHT_HIGH = 0xff56526e;

    @Override
    public void applyDefault() {
        super.applyDefault();

        setColor(PROBLEM_TYPO, FOAM);
        setColor(PROBLEM_WARNING, GOLD);
        setColor(PROBLEM_ERROR, LOVE);

        setColor(WHOLE_BACKGROUND, BASE);
        setColor(TEXT_NORMAL, TEXT);
        setColor(UNDERLINE, TEXT);
        setColor(CURRENT_LINE, HIGHLIGHT_LOW);

        setColor(SCROLL_BAR_TRACK, BASE);
        setColor(SCROLL_BAR_THUMB, OVERLAY);
        setColor(SCROLL_BAR_THUMB_PRESSED, HIGHLIGHT_HIGH);


        setColor(SELECTION_INSERT, TEXT);
        setColor(SELECTION_HANDLE, TEXT);
        setColor(SELECTED_TEXT_BACKGROUND, HIGHLIGHT_MED);

        setColor(LINE_DIVIDER, BASE);
        setColor(LINE_NUMBER_BACKGROUND, BASE);
        setColor(LINE_NUMBER, MUTED);


        setColor(TSAttribute, IRIS);
        setColor(TSAnnotation, IRIS);
        setColor(TSBoolean, ROSE);
        setColor(TSCharacter, GOLD);
        setColor(TSComment, MUTED);
        setColor(TSConditional, PINE);
        setColor(TSConstant, FOAM);
        setColor(TSConstBuiltin, LOVE);
        setColor(TSConstMacro, IRIS);
        setColor(TSConstructor, FOAM);
        setColor(TSEmphasis, SUBTLE);
        setColor(TSError, TEXT);
        setColor(TSException, PINE);
        setColor(TSField, FOAM);
        setColor(TSFloat, GOLD);
        setColor(TSFunction, ROSE);
        setColor(TSFuncBuiltin, LOVE);
        setColor(TSFuncMarco, IRIS);
        setColor(TSInclude, PINE);
        setColor(TSKeyword, PINE);
        setColor(TSKeywordReturn, PINE);
        setColor(TSKeywordFunction, PINE);
        setColor(TSKeywordOperator, PINE);
        setColor(TSLabel, FOAM);
        setColor(TSLiteral, GOLD);
        setColor(TSMethod, ROSE);
        setColor(TSNamespace, IRIS);
        setColor(TSNone, SUBTLE);
        setColor(TSNumber, GOLD);
        setColor(TSOperator, SUBTLE);
        setColor(TSParameter, IRIS);
        setColor(TSParameterReference, IRIS);
        setColor(TSProperty, IRIS);
        setColor(TSPunctBracket, SUBTLE);
        setColor(TSPunctDelimiter, SUBTLE);
        setColor(TSPunctSpecial, SUBTLE);
        setColor(TSRepeat, PINE);
        setColor(TSStrike, SUBTLE);
        setColor(TSString, GOLD);
        setColor(TSStringEscape, PINE);
        setColor(TSStringRegex, GOLD);
        setColor(TSSymbol, ROSE);
        setColor(TSTag, FOAM);
        setColor(TSTagDelimiter, SUBTLE);
        setColor(TSText, TEXT);
        setColor(TSStrong, SUBTLE);
        setColor(TSTitle, IRIS);
        setColor(TSType, FOAM);
        setColor(TSTypeBuiltin, FOAM);
        setColor(TSURL, IRIS);
        setColor(TSNote, IRIS);
        setColor(TSWarning, GOLD);
        setColor(TSDanger, LOVE);
        setColor(TSUnderline, SUBTLE);
        setColor(TSVariable, TEXT);
        setColor(TSVariableBuiltin, LOVE);
    }
}
