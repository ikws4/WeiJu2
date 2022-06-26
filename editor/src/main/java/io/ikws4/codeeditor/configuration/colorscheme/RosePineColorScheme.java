package io.ikws4.codeeditor.configuration.colorscheme;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.configuration.SyntaxColorScheme;

class RosePineColorScheme implements ColorScheme {
    @Override
    public int getBackgroundColor() {
        return 0xFF232136;
    }

    @Override
    public int getTextColor() {
        return 0xFFE0DEF4;
    }

    @Override
    public int getGutterColor() {
        return 0xFF232136;
    }

    @Override
    public int getGutterDividerColor() {
        return 0xFF232136;
    }

    @Override
    public int getGutterTextColor() {
        return 0xFF6E6A86;
    }

    @Override
    public int getGutterActiveTextColor() {
        return 0xFFE0DEF4;
    }

    @Override
    public int getCursorLineColor() {
        return 0xFF2A283E;
    }

    @Override
    public int getSelectionColor() {
        return 0xFF2A283E;
    }

    @Override
    public int getCompletionMenuBackgroundColor() {
        return 0xFF232136;
    }

    @Override
    public int getIndentColor() {
        return 0xFF6E6A86;
    }

    @Override
    public SyntaxColorScheme getSyntaxColorScheme() {
        return new SyntaxColorScheme() {
            @Override
            public int getAnnotationColor() {
                return 0xFFC4A7E7;
            }

            @Override
            public int getConstantColor() {
                return 0xFF9CCFD8;
            }

            @Override
            public int getCommentColor() {
                return 0xFF6E6A86;
            }

            @Override
            public int getNumberColor() {
                return 0xFFF6C177;
            }

            @Override
            public int getOperatorColor() {
                return 0xFF908caa;
            }

            @Override
            public int getKeywordColor() {
                return 0xFF3E8FB0;
            }

            @Override
            public int getTypeColor() {
                return 0xFF9CCFD8;
            }

            @Override
            public int getMethodColor() {
                return 0xFFEA9A97;
            }

            @Override
            public int getStringColor() {
                return 0xFFF6C177;
            }
        };
    }
}
