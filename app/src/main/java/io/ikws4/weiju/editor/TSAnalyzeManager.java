package io.ikws4.weiju.editor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import io.github.rosemoe.sora.lang.analysis.AnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.SimpleAnalyzeManager;
import io.github.rosemoe.sora.lang.analysis.StyleReceiver;
import io.github.rosemoe.sora.lang.styling.MappedSpans;
import io.github.rosemoe.sora.lang.styling.Span;
import io.github.rosemoe.sora.lang.styling.Spans;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.CharPosition;
import io.github.rosemoe.sora.text.ContentReference;
import io.ikws4.jsitter.TSNode;
import io.ikws4.jsitter.TSParser;
import io.ikws4.jsitter.TSQuery;
import io.ikws4.jsitter.TSQueryCapture;
import io.ikws4.jsitter.TSQueryMatch;
import io.ikws4.jsitter.TSTree;
import io.ikws4.weiju.util.Logger;

public class TSAnalyzeManager extends SimpleAnalyzeManager<Object> {
    private final static Map<String, TSHighlightType> hlmap;

    static {
        System.loadLibrary("jsitter");

        hlmap = new HashMap<>();
        hlmap.put("annotation", TSHighlightType.Annotation);
        hlmap.put("attribute", TSHighlightType.Attribute);
        hlmap.put("boolean", TSHighlightType.Boolean);
        hlmap.put("character", TSHighlightType.Character);
        hlmap.put("comment", TSHighlightType.Comment);
        hlmap.put("conditional", TSHighlightType.Conditional);
        hlmap.put("constant", TSHighlightType.Constant);
        hlmap.put("constant.builtin", TSHighlightType.ConstBuiltin);
        hlmap.put("constant.macro", TSHighlightType.ConstMacro);
        hlmap.put("constructor", TSHighlightType.Constructor);
        hlmap.put("error", TSHighlightType.Error);
        hlmap.put("exception", TSHighlightType.Exception);
        hlmap.put("field", TSHighlightType.Field);
        hlmap.put("float", TSHighlightType.Float);
        hlmap.put("function", TSHighlightType.Function);
        hlmap.put("function.builtin", TSHighlightType.FuncBuiltin);
        hlmap.put("function.macro", TSHighlightType.FuncMarco);
        hlmap.put("include", TSHighlightType.Include);
        hlmap.put("keyword", TSHighlightType.Keyword);
        hlmap.put("keyword.return", TSHighlightType.KeywordReturn);
        hlmap.put("keyword.function", TSHighlightType.KeywordFunction);
        hlmap.put("keyword.operator", TSHighlightType.KeywordOperator);
        hlmap.put("label", TSHighlightType.Label);
        hlmap.put("method", TSHighlightType.Method);
        hlmap.put("namespace", TSHighlightType.Namespace);
        hlmap.put("none", TSHighlightType.None);
        hlmap.put("number", TSHighlightType.Number);
        hlmap.put("operator", TSHighlightType.Operator);
        hlmap.put("parameter", TSHighlightType.Parameter);
        hlmap.put("parameter.reference", TSHighlightType.ParameterReference);
        hlmap.put("property", TSHighlightType.Property);
        hlmap.put("punctuation.delimiter", TSHighlightType.PunctDelimiter);
        hlmap.put("punctuation.bracket", TSHighlightType.PunctBracket);
        hlmap.put("punctuation.special", TSHighlightType.PunctSpecial);
        hlmap.put("repeat", TSHighlightType.Repeat);
        hlmap.put("string", TSHighlightType.String);
        hlmap.put("string.regex", TSHighlightType.StringRegex);
        hlmap.put("string.escape", TSHighlightType.StringEscape);
        hlmap.put("symbol", TSHighlightType.Symbol);
        hlmap.put("tag", TSHighlightType.Tag);
        hlmap.put("tag.delimiter", TSHighlightType.TagDelimiter);
        hlmap.put("text", TSHighlightType.Text);
        hlmap.put("text.strong", TSHighlightType.Strong);
        hlmap.put("text.emphasis", TSHighlightType.Emphasis);
        hlmap.put("text.underline", TSHighlightType.Underline);
        hlmap.put("text.strike", TSHighlightType.Strike);
        hlmap.put("text.title", TSHighlightType.Title);
        hlmap.put("text.literal", TSHighlightType.Literal);
        hlmap.put("text.url", TSHighlightType.URL);
        hlmap.put("text.note", TSHighlightType.Note);
        hlmap.put("text.warning", TSHighlightType.Warning);
        hlmap.put("text.danger", TSHighlightType.Danger);
        hlmap.put("type", TSHighlightType.Type);
        hlmap.put("type.builtin", TSHighlightType.TypeBuiltin);
        hlmap.put("variable", TSHighlightType.Variable);
        hlmap.put("variable.builtin", TSHighlightType.VariableBuiltin);
    }

    private final TSQuery mHighlightQuery;
    private final TSParser mParser;
    private TSTree mTree;

    public TSAnalyzeManager(long language, String highlightQuery) {
        mParser = new TSParser(language);
        mHighlightQuery = new TSQuery(language, highlightQuery);
    }

    @Override
    public void destroy() {
        mHighlightQuery.close();
        mParser.close();
        if (mTree != null) mTree.close();
    }

    private final StringBuilder textContainer = new StringBuilder();

    @Override
    protected Styles analyze(StringBuilder text, SimpleAnalyzeManager<Object>.Delegate<Object> delegate) {
        mTree = mParser.parse(textContainer.toString(), mTree);
        int lastline = 0;
        MappedSpans.Builder builder = new MappedSpans.Builder();
        // for (TSQueryMatch match : mHighlightQuery.matchIter(mTree.getRoot())) {
        //     TSQueryCapture capture = match.getCapture(0);
        //     TSNode node = capture.getNode();
        //     int line = node.getStartRow();
        //
        //     Span span = buildSpan(hlmap.get(capture.getName()), node.getStartColumn());
        //     builder.add(line, span);
        //     lastline = Math.max(lastline, line);
        // }
        // builder.determine(lastline);

        return new Styles(builder.build());
    }

    private Span buildSpan(TSHighlightType type, int start) {
        switch (type) {
            case Annotation:
                return Span.obtain(start, TSEditorColorScheme.TSAnnotation);
            case Attribute:
                return Span.obtain(start, TSEditorColorScheme.TSAttribute);
            case Boolean:
                return Span.obtain(start, TSEditorColorScheme.TSBoolean);
            case Character:
                return Span.obtain(start, TSEditorColorScheme.TSCharacter);
            case Comment:
                return Span.obtain(start, TSEditorColorScheme.TSComment);
            case Conditional:
                return Span.obtain(start, TSEditorColorScheme.TSConditional);
            case Constant:
                return Span.obtain(start, TSEditorColorScheme.TSConstant);
            case ConstBuiltin:
                return Span.obtain(start, TSEditorColorScheme.TSConstBuiltin);
            case ConstMacro:
                return Span.obtain(start, TSEditorColorScheme.TSConstMacro);
            case Constructor:
                return Span.obtain(start, TSEditorColorScheme.TSConstructor);
            case Error:
                return Span.obtain(start, TSEditorColorScheme.TSError);
            case Exception:
                return Span.obtain(start, TSEditorColorScheme.TSException);
            case Field:
                return Span.obtain(start, TSEditorColorScheme.TSField);
            case Float:
                return Span.obtain(start, TSEditorColorScheme.TSFloat);
            case Function:
                return Span.obtain(start, TSEditorColorScheme.TSFunction);
            case FuncBuiltin:
                return Span.obtain(start, TSEditorColorScheme.TSFuncBuiltin);
            case FuncMarco:
                return Span.obtain(start, TSEditorColorScheme.TSFuncMarco);
            case Include:
                return Span.obtain(start, TSEditorColorScheme.TSInclude);
            case Keyword:
                return Span.obtain(start, TSEditorColorScheme.TSKeyword);
            case KeywordReturn:
                return Span.obtain(start, TSEditorColorScheme.TSKeywordReturn);
            case KeywordFunction:
                return Span.obtain(start, TSEditorColorScheme.TSKeywordFunction);
            case KeywordOperator:
                return Span.obtain(start, TSEditorColorScheme.TSKeywordOperator);
            case Label:
                return Span.obtain(start, TSEditorColorScheme.TSLabel);
            case Method:
                return Span.obtain(start, TSEditorColorScheme.TSMethod);
            case Namespace:
                return Span.obtain(start, TSEditorColorScheme.TSNamespace);
            case None:
                return Span.obtain(start, TSEditorColorScheme.TSNone);
            case Number:
                return Span.obtain(start, TSEditorColorScheme.TSNumber);
            case Operator:
                return Span.obtain(start, TSEditorColorScheme.TSOperator);
            case Parameter:
                return Span.obtain(start, TSEditorColorScheme.TSParameter);
            case ParameterReference:
                return Span.obtain(start, TSEditorColorScheme.TSParameterReference);
            case Property:
                return Span.obtain(start, TSEditorColorScheme.TSProperty);
            case PunctDelimiter:
                return Span.obtain(start, TSEditorColorScheme.TSPunctDelimiter);
            case PunctBracket:
                return Span.obtain(start, TSEditorColorScheme.TSPunctBracket);
            case PunctSpecial:
                return Span.obtain(start, TSEditorColorScheme.TSPunctSpecial);
            case Repeat:
                return Span.obtain(start, TSEditorColorScheme.TSRepeat);
            case String:
                return Span.obtain(start, TSEditorColorScheme.TSString);
            case StringRegex:
                return Span.obtain(start, TSEditorColorScheme.TSStringRegex);
            case StringEscape:
                return Span.obtain(start, TSEditorColorScheme.TSStringEscape);
            case Symbol:
                return Span.obtain(start, TSEditorColorScheme.TSSymbol);
            case Tag:
                return Span.obtain(start, TSEditorColorScheme.TSTag);
            case TagDelimiter:
                return Span.obtain(start, TSEditorColorScheme.TSTagDelimiter);
            case Text:
                return Span.obtain(start, TSEditorColorScheme.TSText);
            case Strong:
                return Span.obtain(start, TSEditorColorScheme.TSStrong);
            case Emphasis:
                return Span.obtain(start, TSEditorColorScheme.TSEmphasis);
            case Underline:
                return Span.obtain(start, TSEditorColorScheme.TSUnderline);
            case Strike:
                return Span.obtain(start, TSEditorColorScheme.TSStrike);
            case Title:
                return Span.obtain(start, TSEditorColorScheme.TSTitle);
            case Literal:
                return Span.obtain(start, TSEditorColorScheme.TSLiteral);
            case URL:
                return Span.obtain(start, TSEditorColorScheme.TSURL);
            case Note:
                return Span.obtain(start, TSEditorColorScheme.TSNote);
            case Warning:
                return Span.obtain(start, TSEditorColorScheme.TSWarning);
            case Danger:
                return Span.obtain(start, TSEditorColorScheme.TSDanger);
            case Type:
                return Span.obtain(start, TSEditorColorScheme.TSType);
            case TypeBuiltin:
                return Span.obtain(start, TSEditorColorScheme.TSTypeBuiltin);
            case Variable:
                return Span.obtain(start, TSEditorColorScheme.TSVariable);
            case VariableBuiltin:
                return Span.obtain(start, TSEditorColorScheme.TSVariableBuiltin);
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
