package io.ikws4.weiju.editor;

import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;

class RosepineColorScheme extends TextMateColorScheme {
    public static final int TRANSPARENT = 0x00000000;
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

    public RosepineColorScheme() throws Exception {
        super(ThemeRegistry.getInstance(), ThemeRegistry.getInstance().findThemeByFileName("rose-pine"));
    }

    @Override
    public void applyDefault() {
        setColor(PROBLEM_TYPO, FOAM);
        setColor(PROBLEM_WARNING, GOLD);
        setColor(PROBLEM_ERROR, LOVE);

        setColor(WHOLE_BACKGROUND, BASE);
        setColor(TEXT_NORMAL, TEXT);
        setColor(UNDERLINE, TRANSPARENT);
        setColor(CURRENT_LINE, BASE);

        setColor(SCROLL_BAR_TRACK, BASE);
        setColor(SCROLL_BAR_THUMB, OVERLAY);
        setColor(SCROLL_BAR_THUMB_PRESSED, HIGHLIGHT_HIGH);

        setColor(SELECTION_INSERT, TEXT);
        setColor(SELECTION_HANDLE, TEXT);
        setColor(SELECTED_TEXT_BACKGROUND, HIGHLIGHT_MED);

        setColor(LINE_DIVIDER, BASE);
        setColor(LINE_NUMBER_BACKGROUND, BASE);
        setColor(LINE_NUMBER, MUTED);
        setColor(LINE_NUMBER_CURRENT, TEXT);

        setColor(HIGHLIGHTED_DELIMITERS_BACKGROUND, TRANSPARENT);
        setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, SUBTLE);
        setColor(HIGHLIGHTED_DELIMITERS_UNDERLINE, TRANSPARENT);

        setColor(COMPLETION_WND_BACKGROUND, SURFACE);
        setColor(COMPLETION_WND_TEXT_PRIMARY, TEXT);
        setColor(COMPLETION_WND_TEXT_SECONDARY, SUBTLE);
        setColor(COMPLETION_WND_ITEM_CURRENT, HIGHLIGHT_LOW);
    }
}
