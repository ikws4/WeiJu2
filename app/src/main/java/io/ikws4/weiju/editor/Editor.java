package io.ikws4.weiju.editor;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.ikws4.weiju.R;
import io.ikws4.weiju.editor.lua.LuaLanguage;

public class Editor extends CodeEditor {

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        LuaLanguage language = new LuaLanguage();
        setEditorLanguage(language);

        Typeface font = ResourcesCompat.getFont(context, R.font.jetbrains_mono_regular);
        setTypefaceText(font);

        setColorScheme(new RosepineColorScheme());

        setEdgeEffectColor(RosepineColorScheme.BASE);
        setCursorAnimationEnabled(false);
        setLineNumberEnabled(false);
        setLigatureEnabled(true);
        setScrollBarEnabled(false);
        setCursorWidth(2 * getDpUnit());
        setDividerWidth(0);
        setLineNumberAlign(Paint.Align.RIGHT);

        getComponent(Magnifier.class).setEnabled(false);
    }
}

