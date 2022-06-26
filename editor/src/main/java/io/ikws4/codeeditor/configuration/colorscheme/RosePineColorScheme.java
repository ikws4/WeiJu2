package io.ikws4.codeeditor.configuration.colorscheme;

import io.ikws4.codeeditor.api.configuration.ColorScheme;


class RosePineColorScheme extends ColorScheme {

  public RosePineColorScheme() {
    int base = 0xff232136;
    int surface = 0xff2a273f;
    int overlay = 0xff393552;
    int muted = 0xff6e6a86;
    int subtle = 0xff908caa;
    int text = 0xffe0def4;
    int love = 0xffeb6f92;
    int gold = 0xfff6c177;
    int rose = 0xffea9a97;
    int pine = 0xff3e8fb0;
    int foam = 0xff9ccfd8;
    int iris = 0xffc4a7e7;
    int highlightLow = 0xff2a283e;
    int highlightMed = 0xff44415a;
    int highlightHigh = 0xff56526e;

    ui.background = base;
    ui.foreground = text;
    ui.gutterBackground = base;
    ui.gutterForeground = muted;
    ui.gutterActivedForeground = text;
    ui.cusorLineBackground = highlightLow;
    ui.selectionBackground = highlightLow;
    ui.completionMenuBackground = base;
    ui.completionMenuOutlineBackground = surface;

    syntax.tsAttribute = iris;
    syntax.tsAnnotation = iris;
    syntax.tsBoolean = rose;
    syntax.tsCharacter = gold;
    syntax.tstSCharacterSpecial = rose;
    syntax.tsComment = muted;
    syntax.tsConditional = pine;
    syntax.tsConstant = foam;
    syntax.tsConstBuiltin = love;
    syntax.tsConstMacro = iris;
    syntax.tsConstructor = foam;
    syntax.tsEmphasis = subtle;
    syntax.tsError = love;
    syntax.tsException = pine;
    syntax.tsField = foam;
    syntax.tsFloat = gold;
    syntax.tsFunction = rose;
    syntax.tsFuncBuiltin = love;
    syntax.tsFuncMacro = iris;
    syntax.tsInclude = pine;
    syntax.tsKeyword = pine;
    syntax.tsKeywordReturn = pine;
    syntax.tsKeywordFunction = pine;
    syntax.tsKeywordOperator = pine;
    syntax.tsLabel = foam;
    syntax.tsLiteral = gold;
    syntax.tsMethod = rose;
    syntax.tsNamespace = iris;
    syntax.tsNone = subtle;
    syntax.tsNumber = gold;
    syntax.tsOperator = subtle;
    syntax.tsParameter = iris;
    syntax.tsParameterReference = iris;
    syntax.tsProperty = iris;
    syntax.tsPunctBracket = subtle;
    syntax.tsPunctDelimiter = subtle;
    syntax.tsPunctSpecial = subtle;
    syntax.tsRepeat = pine;
    syntax.tsStrike = subtle;
    syntax.tsString = gold;
    syntax.tsStringEscape = pine;
    syntax.tsStringRegex = gold;
    syntax.tsStringspecial = gold;
    syntax.tsSymbol = rose;
    syntax.tsTag = foam;
    syntax.tsTagDelimiter = subtle;
    syntax.tsText = text;
    syntax.tsStrong = subtle;
    syntax.tsTitle = iris;
    syntax.tsType = foam;
    syntax.tsTypeBuiltin = foam;
    syntax.tsUri = iris;
    syntax.tsNote = iris;
    syntax.tsWarning = gold;
    syntax.tsDanger = love;
    syntax.tsUnderline = subtle;
    syntax.tsVariable = text;
    syntax.tsVariableBuiltin = love;
  }
}
