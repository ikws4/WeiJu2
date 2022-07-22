package io.ikws4.weiju.page.editor;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.weiju.R;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.page.editor.view.EditorSymbolBar;
import io.ikws4.weiju.page.home.view.ScriptListView;

public class EditorFragment extends Fragment {
    public EditorFragment() {
        super(R.layout.editor_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ScriptListView.ScriptItem item = requireArguments().getParcelable("item");

        Editor vEditor = view.findViewById(R.id.editor);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) vEditor.getLayoutParams();
        layoutParams.leftMargin = (int) vEditor.getCharWidth();
        vEditor.setLayoutParams(layoutParams);
        vEditor.setText(item.script);

        vEditor.subscribeEvent(ContentChangeEvent.class, new EventReceiver<ContentChangeEvent>() {
            @Override
            public void onReceive(ContentChangeEvent event, Unsubscribe unsubscribe) {
                if (event.getAction() == ContentChangeEvent.ACTION_DELETE) {
                    Content content = vEditor.getText();
                    char afterChar = content.charAt(event.getChangeStart().index);

                    CharSequence changed = event.getChangedText();
                    if (changed.length() == 1) {
                        char deltedChar = event.getChangedText().charAt(0);
                        SymbolPairMatch pair = vEditor.getEditorLanguage().getSymbolPairs();
                        SymbolPairMatch.Replacement replacement = pair.getCompletion(deltedChar);
                        if (replacement != null && replacement.text.charAt(1) == afterChar) {
                            vEditor.setSelection(event.getChangeStart().line, event.getChangeStart().column + 1);
                            vEditor.deleteText();
                        }
                    }
                }
            }
        });

        EditorSymbolBar vSymbolBar = view.findViewById(R.id.editor_symbol_bar);
        vSymbolBar.registerCallbacks(new EditorSymbolBar.Callbacks() {
            @Override
            public void onClickSymbol(String s) {
                SymbolPairMatch pair = vEditor.getEditorLanguage().getSymbolPairs();
                SymbolPairMatch.Replacement replacement = pair.getCompletion(s.charAt(0));
                Content content = vEditor.getText();
                Cursor cursor = vEditor.getCursor();
                char afterChar = content.charAt(cursor.getRight());
                if (afterChar == s.charAt(0)) {
                    vEditor.moveSelectionRight();
                } else {
                    if (replacement != null) {
                        vEditor.insertText(replacement.text, replacement.selection);
                    } else {
                        vEditor.insertText(s, 1);
                    }
                }
            }
        });
    }
}
