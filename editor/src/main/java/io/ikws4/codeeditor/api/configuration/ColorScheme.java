package io.ikws4.codeeditor.api.configuration;

public class ColorScheme {
  public ColorScheme.UI ui = new UI();
  public ColorScheme.Syntax syntax = new Syntax();

  public static class UI {
    public int background;
    public int foreground;
    public int gutterBackground;
    public int gutterForeground;
    public int gutterActivedForeground;
    public int cusorLineBackground;
    public int selectionBackground;
    public int completionMenuBackground;
  }

  public static class Syntax {
    public int tsAttribute;
    public int tsAnnotation;
    public int tsBoolean;
    public int tsCharacter;
    public int tstSCharacterSpecial;
    public int tsComment;
    public int tsConditional;
    public int tsConstant;
    public int tsConstBuiltin;
    public int tsConstMacro;
    public int tsConstructor;
    public int tsEmphasis;
    public int tsError;
    public int tsException;
    public int tsField;
    public int tsFloat;
    public int tsFunction;
    public int tsFuncBuiltin;
    public int tsFuncMacro;
    public int tsInclude;
    public int tsKeyword;
    public int tsKeywordReturn;
    public int tsKeywordFunction;
    public int tsKeywordOperator;
    public int tsLabel;
    public int tsLiteral;
    public int tsMethod;
    public int tsNamespace;
    public int tsNone;
    public int tsNumber;
    public int tsOperator;
    public int tsParameter;
    public int tsParameterReference;
    public int tsProperty;
    public int tsPunctBracket;
    public int tsPunctDelimiter;
    public int tsPunctSpecial;
    public int tsRepeat;
    public int tsStrike;
    public int tsString;
    public int tsStringEscape;
    public int tsStringRegex;
    public int tsStringspecial;
    public int tsSymbol;
    public int tsTag;
    public int tsTagDelimiter;
    public int tsText;
    public int tsStrong;
    public int tsTitle;
    public int tsType;
    public int tsTypeBuiltin;
    public int tsUri;
    public int tsNote;
    public int tsWarning;
    public int tsDanger;
    public int tsUnderline;
    public int tsVariable;
    public int tsVariableBuiltin;
  }
}

