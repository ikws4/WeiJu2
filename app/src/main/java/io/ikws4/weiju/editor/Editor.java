package io.ikws4.weiju.editor;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.core.content.res.ResourcesCompat;

import org.eclipse.tm4e.core.internal.theme.reader.ThemeReader;
import org.eclipse.tm4e.core.theme.IRawTheme;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.ikws4.weiju.R;

public class Editor extends CodeEditor {

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface font = ResourcesCompat.getFont(context, R.font.jetbrains_mono_regular);
        setTypefaceText(font);

        setEdgeEffectColor(RosepineColorScheme.BASE);
        setCursorAnimationEnabled(false);
        setLineNumberEnabled(false);
        setLigatureEnabled(true);
        setScrollBarEnabled(false);
        setCursorWidth(2 * getDpUnit());
        setDividerWidth(0);
        setLineNumberAlign(Paint.Align.RIGHT);

        getComponent(Magnifier.class).setEnabled(false);

        setLanguageAndTheme("lua", "rose-pine");
    }

    private void setLanguageAndTheme(String lang, String theme) {
        try {
            InputStream in = getContext().getAssets().open("textmate/lang/" + lang + "/language-configuration.json");
            String grammarPath = "textmate/lang/" + lang + "/tmLanguage.json";

            String themePath = "textmate/theme/" + theme + ".json";
            IRawTheme _theme = ThemeReader.readThemeSync(themePath, getContext().getAssets().open(themePath));

            TextMateLanguage language = TextMateLanguage.create(grammarPath, getContext().getAssets().open(grammarPath), new InputStreamReader(in), _theme);
            setEditorLanguage(language);
            setColorScheme(new RosepineColorScheme(_theme));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

