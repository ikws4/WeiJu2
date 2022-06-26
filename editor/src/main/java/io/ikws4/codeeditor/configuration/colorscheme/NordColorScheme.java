package io.ikws4.codeeditor.configuration.colorscheme;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.configuration.SyntaxColorScheme;

class NordColorScheme implements ColorScheme {
    @Override
    public int getBackgroundColor() {
        return 0xFF2E3440;
    }

    @Override
    public int getTextColor() {
        return 0xFFD8DEE9;
    }

    @Override
    public int getGutterColor() {
        return 0xFF2E3440;
    }

    @Override
    public int getGutterDividerColor() {
        return 0xFF2E3440;
    }

    @Override
    public int getGutterTextColor() {
        return 0xFF4C566A;
    }

    @Override
    public int getGutterActiveTextColor() {
        return 0xFFD8DEE9;
    }

    @Override
    public int getCursorLineColor() {
        return 0xFF3B4252;
    }

    @Override
    public int getSelectionColor() {
        return 0xFF3B4252;
    }

    @Override
    public int getCompletionMenuBackgroundColor() {
        return 0xFF4C566A;
    }

    @Override
    public int getIndentColor() {
        return 0xFF4C566A;
    }

    public SyntaxColorScheme getSyntaxColorScheme() {
        return new SyntaxColorScheme() {
            @Override
            public int getAnnotationColor() {
                return 0XFFD08770;
            }

            @Override
            public int getCommentColor() {
                return 0xFF4C566A;
            }

            @Override
            public int getNumberColor() {
                return 0xFFB48EAD;
            }

            @Override
            public int getOperatorColor() {
                return 0xFF81A1C1;
            }

            @Override
            public int getKeywordColor() {
                return 0xFF81A1C1;
            }

            @Override
            public int getTypeColor() {
                return 0xFF8FBCBB;
            }

            @Override
            public int getConstantColor() {
                return 0xFFD8DEE9;
            }

            @Override
            public int getMethodColor() {
                return 0xFF88C0D0;
            }

            @Override
            public int getStringColor() {
                return 0xFFA3BE8C;
            }
        };
    }
}
