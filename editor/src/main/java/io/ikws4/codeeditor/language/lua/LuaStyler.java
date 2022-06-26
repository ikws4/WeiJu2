package io.ikws4.codeeditor.language.lua;

import android.util.Log;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.document.markup.Markup;
import io.ikws4.codeeditor.language.TSHighlightType;
import io.ikws4.codeeditor.api.document.markup.SyntaxMarkup;
import io.ikws4.codeeditor.language.TSLanguageStyler;
import io.ikws4.jsitter.TSLanguages;

class LuaStyler extends TSLanguageStyler {
    private static final String TAG = "JavaStyler";

    private final Formatter mFormatter;
    private static LuaStyler sInstance;

    private LuaStyler() {
        super(TSLanguages.lua(), new LuaQuery());
        JavaFormatterOptions options = JavaFormatterOptions.builder()
                .style(JavaFormatterOptions.Style.AOSP)
                .build();
        mFormatter = new Formatter(options);
    }

    public static LuaStyler getInstance() {
        if (sInstance == null) {
            sInstance = new LuaStyler();
        }
        return sInstance;
    }

    @Override
    public String format(String source) {
        try {
            return mFormatter.formatSource(source);
        } catch (FormatterException e) {
            Log.w(TAG, "format: ", e);
        }
        return source;
    }

    @Override
    protected Markup onBuildMarkup(TSHighlightType type, int start, int end, ColorScheme.Syntax scheme) {
        switch (type) {
            case Annotation:
                return new SyntaxMarkup(scheme.tsAnnotation, start, end);
            case Attribute:
                return new SyntaxMarkup(scheme.tsAttribute, start, end);
            case Boolean:
                return new SyntaxMarkup(scheme.tsBoolean, start, end);
            case Character:
                return new SyntaxMarkup(scheme.tsCharacter, start, end);
            case Comment:
                return new SyntaxMarkup(scheme.tsComment, start, end);
            case Conditional:
                return new SyntaxMarkup(scheme.tsConditional, start, end);
            case Constant:
                return new SyntaxMarkup(scheme.tsConstant, start, end);
            case ConstBuiltin:
                return new SyntaxMarkup(scheme.tsConstBuiltin, start, end);
            case ConstMacro:
                return new SyntaxMarkup(scheme.tsConstMacro, start, end);
            case Constructor:
                return new SyntaxMarkup(scheme.tsConstructor, start, end);
            case Error:
                return new SyntaxMarkup(scheme.tsError, start, end);
            case Exception:
                return new SyntaxMarkup(scheme.tsException, start, end);
            case Field:
                return new SyntaxMarkup(scheme.tsField, start, end);
            case Float:
                return new SyntaxMarkup(scheme.tsFloat, start, end);
            case Function:
                return new SyntaxMarkup(scheme.tsFunction, start, end);
            case FuncBuiltin:
                return new SyntaxMarkup(scheme.tsFuncBuiltin, start, end);
            case FuncMarco:
                return new SyntaxMarkup(scheme.tsFuncMacro, start, end);
            case Include:
                return new SyntaxMarkup(scheme.tsInclude, start, end);
            case Keyword:
                return new SyntaxMarkup(scheme.tsKeyword, start, end);
            case KeywordFunction:
                return new SyntaxMarkup(scheme.tsKeywordFunction, start, end);
            case KeywordOperator:
                return new SyntaxMarkup(scheme.tsKeywordOperator, start, end);
            case Label:
                return new SyntaxMarkup(scheme.tsLabel, start, end);
            case Method:
                return new SyntaxMarkup(scheme.tsMethod, start, end);
            case Namespace:
                return new SyntaxMarkup(scheme.tsNamespace, start, end);
            case None:
                return new SyntaxMarkup(scheme.tsNone, start, end);
            case Number:
                return new SyntaxMarkup(scheme.tsNumber, start, end);
            case Operator:
                return new SyntaxMarkup(scheme.tsOperator, start, end);
            case Parameter:
                return new SyntaxMarkup(scheme.tsParameter, start, end);
            case ParameterReference:
                return new SyntaxMarkup(scheme.tsParameterReference, start, end);
            case Property:
                return new SyntaxMarkup(scheme.tsProperty, start, end);
            case PunctDelimiter:
                return new SyntaxMarkup(scheme.tsPunctDelimiter, start, end);
            case PunctBracket:
                return new SyntaxMarkup(scheme.tsPunctBracket, start, end);
            case PunctSpecial:
                return new SyntaxMarkup(scheme.tsPunctSpecial, start, end);
            case Repeat:
                return new SyntaxMarkup(scheme.tsRepeat, start, end);
            case String:
                return new SyntaxMarkup(scheme.tsString, start, end);
            case StringRegex:
                return new SyntaxMarkup(scheme.tsStringRegex, start, end);
            case StringEscape:
                return new SyntaxMarkup(scheme.tsStringEscape, start, end);
            case Symbol:
                return new SyntaxMarkup(scheme.tsSymbol, start, end);
            case Tag:
                return new SyntaxMarkup(scheme.tsTag, start, end);
            case TagDelimiter:
                return new SyntaxMarkup(scheme.tsTagDelimiter, start, end);
            case Text:
                return new SyntaxMarkup(scheme.tsText, start, end);
            case Strong:
                return new SyntaxMarkup(scheme.tsStrong, start, end);
            case Emphasis:
                return new SyntaxMarkup(scheme.tsEmphasis, start, end);
            case Underline:
                return new SyntaxMarkup(scheme.tsUnderline, start, end);
            case Strike:
                return new SyntaxMarkup(scheme.tsStrike, start, end);
            case Title:
                return new SyntaxMarkup(scheme.tsTitle, start, end);
            case Literal:
                return new SyntaxMarkup(scheme.tsLiteral, start, end);
            case URL:
                return new SyntaxMarkup(scheme.tsUri, start, end);
            case Note:
                return new SyntaxMarkup(scheme.tsNone, start, end);
            case Warning:
                return new SyntaxMarkup(scheme.tsWarning, start, end);
            case Danger:
                return new SyntaxMarkup(scheme.tsDanger, start, end);
            case Type:
                return new SyntaxMarkup(scheme.tsType, start, end);
            case TypeBuiltin:
                return new SyntaxMarkup(scheme.tsTypeBuiltin, start, end);
            case Variable:
                return new SyntaxMarkup(scheme.tsVariable, start, end);
            case VariableBuiltin:
                return new SyntaxMarkup(scheme.tsVariableBuiltin, start, end);
        }
        return null;
    }
}
