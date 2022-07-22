package io.ikws4.weiju.editor;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import org.eclipse.tm4e.core.internal.theme.reader.ThemeReader;
import org.eclipse.tm4e.core.theme.IRawTheme;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.lang.completion.CompletionItem;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.github.rosemoe.sora.widget.component.DefaultCompletionLayout;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.ikws4.weiju.util.UnitConverter;

public class Editor extends CodeEditor {
    private float mCharWidth = 0;

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCharWidth = getTextPaint().measureText("1") / 2;

        Typeface font = ResourcesCompat.getFont(context, R.font.jetbrains_mono_regular);
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
        replaceComponent(EditorAutoCompletion.class, new AutoCompletion(this));

        setLanguageAndTheme();

        subscribeEvent(ContentChangeEvent.class, new EventReceiver<ContentChangeEvent>() {
            @Override
            public void onReceive(ContentChangeEvent event, Unsubscribe unsubscribe) {
                if (event.getAction() == ContentChangeEvent.ACTION_DELETE) {
                    Content content = getText();
                    char afterChar = content.charAt(event.getChangeStart().index);

                    CharSequence changed = event.getChangedText();
                    if (changed.length() == 1) {
                        char deltedChar = event.getChangedText().charAt(0);
                        SymbolPairMatch pair = getEditorLanguage().getSymbolPairs();
                        SymbolPairMatch.Replacement replacement = pair.getCompletion(deltedChar);
                        if (replacement != null && replacement.text.charAt(1) == afterChar) {
                            setSelection(event.getChangeStart().line, event.getChangeStart().column + 1);
                            deleteText();
                        }
                    }
                }
            }
        });

        setAutoCompletionItemAdapter(new ComopletionAdapter(this));
        setCompletionWndPositionMode(CodeEditor.WINDOW_POS_MODE_FOLLOW_CURSOR_ALWAYS);
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

    private static class AutoCompletion extends EditorAutoCompletion {

        public AutoCompletion(CodeEditor editor) {
            super(editor);
            setLayout(new CompletionLayout());
        }

        @Override
        public void setLocation(int x, int y) {
            x -= UnitConverter.dp(22);
            y -= getEditor().getTextSizePx() / 2;
            super.setLocation(x, y);
        }
    }

    private static class CompletionLayout extends DefaultCompletionLayout {
        private View mRoot;

        @Override
        public View inflate(Context context) {
            mRoot = super.inflate(context);
            return mRoot;
        }

        @Override
        public void onApplyColorScheme(EditorColorScheme colorScheme) {
            GradientDrawable gd = new GradientDrawable();
            gd.setStroke(1, colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER));
            gd.setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND));
            mRoot.setBackground(gd);
        }
    }

    private static class ComopletionAdapter extends EditorCompletionAdapter {
        private static final Map<String, String> KIND = new HashMap<>();
        private final WeakReference<Editor> mEditor;

        static {
            KIND.put("Identifier", "");

            KIND.put("Text", "");
            KIND.put("Method", "");
            KIND.put("Function", "");
            KIND.put("Constructor", "");
            KIND.put("Field", "");
            KIND.put("Variable", "");
            KIND.put("Class", "");
            KIND.put("Interface", "");
            KIND.put("Module", "");
            KIND.put("Property", "");
            KIND.put("Unit", "");
            KIND.put("Value", "");
            KIND.put("Enum", "");
            KIND.put("Keyword", "");
            KIND.put("Snippet", "");
            KIND.put("Color", "");
            KIND.put("File", "");
            KIND.put("Reference", "");
            KIND.put("Folder", "");
            KIND.put("EnumMember", "");
            KIND.put("Constant", "");
            KIND.put("Struct", "");
            KIND.put("Event", "");
            KIND.put("Operator", "");
            KIND.put("TypeParameter", "");
        }

        private ComopletionAdapter(Editor editor) {
            mEditor = new WeakReference<>(editor);
        }

        @Override
        public int getItemHeight() {
            return UnitConverter.dp(40);
        }

        @Override
        protected View getView(int position, View view, ViewGroup parent, boolean isCurrentCursorPosition) {
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.completion_item, parent, false);
            }
            CompletionItem item = getItem(position);

            TextView vItem = view.findViewById(R.id.tv_item);
            Spannable spannable = new SpannableStringBuilder()
                .append(KIND.get(item.desc), new ForegroundColorSpan(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY)), Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                .append(" ")
                .append(item.label, new ForegroundColorSpan(getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY)), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            vItem.setText(spannable);
            vItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEditor.get().getTextSizePx());

            view.setTag(position);
            if (isCurrentCursorPosition) {
                view.setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT));
            } else {
                view.setBackgroundColor(0);
            }

            return view;
        }
    }
}

