package io.ikws4.codeeditor.language;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.document.markup.Markup;
import io.ikws4.codeeditor.api.language.LanguageStyler;
import io.ikws4.jsitter.TSNode;
import io.ikws4.jsitter.TSParser;
import io.ikws4.jsitter.TSQuery;
import io.ikws4.jsitter.TSQueryCapture;
import io.ikws4.jsitter.TSTree;

public abstract class TSLanguageStyler implements LanguageStyler {
    private static final String TAG = "TSLanguageStyler";

    private final static HashMap<String, TSHighlightType> hlmap;
    private final TSParser mParser;
    private final TSQuery mHighlightQuery;
    private final TSQuery mIndentQuery;
    private TSTree mTree;

    static {
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

    /**
     * @param language see {@link io.ikws4.jsitter.TSLanguages}
     */
    public TSLanguageStyler(long language, TSLangaugeQuery queryScm) {
        mParser = new TSParser(language);
        mHighlightQuery = new TSQuery(language, queryScm.highlight());
        mIndentQuery = new TSQuery(language, queryScm.indent());
    }

    @Override
    public void editSyntaxTree(int startByte, int oldEndByte, int newEndByte, int startRow, int startColumn, int oldEndRow, int oldEndColumn, int newEndRow, int newEndColumn) {
        if (mTree == null) return;
        mTree.edit(startByte, oldEndByte, newEndByte, startRow, startColumn, oldEndRow, oldEndColumn, newEndRow, newEndColumn);
    }

    /**
     * Reference <a href="https://github.com/nvim-treesitter/nvim-treesitter/blob/master/lua/nvim-treesitter/indent.lua">https://github.com/nvim-treesitter/nvim-treesitter/blob/master/lua/nvim-treesitter/indent.lua</a>
     */
    @Override
    public int getIndentLevel(int line, int prevnonblankLine) {
        int level = 0;

        TSNode root = mTree.getRoot();
        TSNode curr = TSUtil.getNodeAtLine(root, line);

        HashMap<TSNode, TSIndentType> queryMap = new HashMap<>();
        for (TSQueryCapture capture : mIndentQuery.captureIter(root)) {
            TSIndentType type = TSIndentType.Ignore;
            switch (capture.getName()) {
                case "indent":
                    type = TSIndentType.Indent;
                    break;
                case "branch":
                    type = TSIndentType.Branch;
                    break;
                case "return":
                    type = TSIndentType.Return;
                    break;
            }
            queryMap.put(capture.getNode(), type);
        }

        if (curr == null) {
            if (prevnonblankLine != line) {
                TSNode prevNode = TSUtil.getNodeAtLine(root, prevnonblankLine);
                boolean usePrev = prevNode != null && prevNode.getEndRow() < line;
                usePrev &= queryMap.get(prevNode) != TSIndentType.Return;
                if (usePrev) {
                    curr = prevNode;
                }
            }
        }

        if (curr == null) {
            TSNode wrapper = root.decendantForRange(line, 0, line, -1);
            assert wrapper != null;

            curr = wrapper.getChild(0);
            if (curr == null) curr = wrapper;

            if (queryMap.get(wrapper) == TSIndentType.Indent && wrapper != root) {
                level = 1;
            }
        }

        while (curr != null && queryMap.get(curr) == TSIndentType.Branch) {
            curr = curr.getParent();
        }

        boolean first = true;
        assert curr != null;

        int prevRow = curr.getStartRow();

        while (curr != null) {
            if (TSIndentType.Ignore == queryMap.get(curr) && curr.getStartRow() < line && curr.getEndRow() > line) {
                return 0;
            }

            int row = curr.getStartRow();
            if (!first && TSIndentType.Indent == queryMap.get(curr) && prevRow != row) {
                level++;
                prevRow = row;
            }

            curr = curr.getParent();
            first = false;
        }

        return level;
    }

    @Override
    public List<Markup> process(String source, ColorScheme.Syntax scheme) {
        List<Markup> markups = new ArrayList<>();
        parse(source);

        for (TSQueryCapture capture : mHighlightQuery.captureIter(mTree.getRoot())) {
            TSNode node = capture.getNode();
            Markup markup = onBuildMarkup(hlmap.get(capture.getName()), node.getStartByte(), node.getEndByte(), scheme);
            if (markup != null) markups.add(markup);
        }

        return markups;
    }

    @Nullable
    protected abstract Markup onBuildMarkup(TSHighlightType type, int start, int end, ColorScheme.Syntax scheme);

    private void parse(String source) {
        synchronized (this) {
            mTree = mParser.parse(source, mTree);
        }
    }
}
