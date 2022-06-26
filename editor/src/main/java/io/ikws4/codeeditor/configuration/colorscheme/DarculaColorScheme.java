package io.ikws4.codeeditor.configuration.colorscheme;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.configuration.SyntaxColorScheme;

class DarculaColorScheme implements ColorScheme {
    @Override
    public int getBackgroundColor() {
        return 0xFF303030;
    }

    @Override
    public int getTextColor() {
        return 0xFFABB7C5;
    }

    @Override
    public int getGutterColor() {
        return 0xFF313335;
    }

    @Override
    public int getGutterDividerColor() {
        return 0xFF555555;
    }

    @Override
    public int getGutterTextColor() {
        return 0xFF616366;
    }

    @Override
    public int getGutterActiveTextColor() {
        return 0xFFA4A3A3;
    }

    @Override
    public int getCursorLineColor() {
        return 0xFF3A3A3A;
    }

    @Override
    public int getSelectionColor() {
        return 0xFF28427F;
    }

    @Override
    public int getCompletionMenuBackgroundColor() {
        // FIXME: this color need make constract with #getBackgroundColor
        return 0xFF303030;
    }

    @Override
    public int getIndentColor() {
        return 0xFF606060;
    }

    @Override
    public SyntaxColorScheme getSyntaxColorScheme() {
        return new SyntaxColorScheme() {
            @Override
            public int getAnnotationColor() {
                return 0xFFBABABA;
            }

            @Override
            public int getCommentColor() {
                return 0xFF66747B;
            }

            @Override
            public int getConstantColor() {
                return 0xFFEC7600;
            }

            @Override
            public int getNumberColor() {
                return 0xFF6897BB;
            }

            @Override
            public int getOperatorColor() {
                return 0xFFE8E2B7;
            }

            @Override
            public int getKeywordColor() {
                return 0xFFEC7600;
            }

            @Override
            public int getTypeColor() {
                return 0xFFEC7600;
            }

            @Override
            public int getMethodColor() {
                return 0xFFFEC76C;
            }

            @Override
            public int getStringColor() {
                return 0xFF6E875A;
            }
        };
    }
}

