package io.ikws4.weiju.page.editor;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.weiju.R;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.page.editor.view.EditorSymbolBar;
import io.ikws4.weiju.page.home.view.ScriptListView;

public class EditorFragment extends Fragment implements MenuProvider {
    public EditorFragment() {
        super(R.layout.editor_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        ScriptListView.ScriptItem item = requireArguments().getParcelable("item");

        Editor vEditor = view.findViewById(R.id.editor);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) vEditor.getLayoutParams();
        layoutParams.leftMargin = (int) vEditor.getCharWidth();
        vEditor.setLayoutParams(layoutParams);
        vEditor.setText(item.script);

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

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();

        menuInflater.inflate(R.menu.editor_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.help) {
            Toast.makeText(getContext(), "TODO: Help", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
    }
}
