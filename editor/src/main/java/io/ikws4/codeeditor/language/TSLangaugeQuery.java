package io.ikws4.codeeditor.language;

/**
 * Provide query.
 * <p>
 * A query are what treesitter uses to extract informations from the syntax tree.
 * </p>
 * <p>
 * see <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#pattern-matching-with-queries">How to write query?</a>
 * </p>
 */
public interface TSLangaugeQuery {
    String highlight();

    String indent();
}
