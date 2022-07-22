package io.ikws4.weiju.editor;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import org.eclipse.tm4e.core.internal.theme.reader.ThemeReader;
import org.eclipse.tm4e.core.theme.IRawTheme;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.Magnifier;

public class Editor extends CodeEditor {
    private float mCharWidth = 0;

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCharWidth = getTextPaint().measureText("1") / 2;

        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/jetbrains_mono_regular.ttf");
        setTypefaceText(font);

        setEdgeEffectColor(RosepineColorScheme.BASE);
        setCursorAnimationEnabled(false);
        setHighlightCurrentLine(false);

        setLigatureEnabled(true);
        setScrollBarEnabled(false);
        setCursorWidth(2 * getDpUnit());
        setDividerWidth(0);
        setDividerMargin(mCharWidth);
        setLineNumberAlign(Paint.Align.RIGHT);
        setTabWidth(2);

        getComponent(Magnifier.class).setEnabled(false);

        setLanguageAndTheme();
    }

    private void setLanguageAndTheme() {
        try {
            String themePath = "textmate/theme/rose-pine.json";
            IRawTheme _theme = ThemeReader.readThemeSync(themePath, getContext().getAssets().open(themePath));

            LuaLanguage language = new LuaLanguage(this, _theme);
            language.setTabSize(getTabWidth());
            setEditorLanguage(language);
            setColorScheme(new RosepineColorScheme(_theme));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getCharWidth() {
        return mCharWidth;
    }

    @Override
    public void insertText(String text, int selectionOffset) {
        super.insertText(text, selectionOffset);
    }
}

