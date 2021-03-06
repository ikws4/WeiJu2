package io.ikws4.weiju.page.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.Cursor;
import io.github.rosemoe.sora.widget.SymbolPairMatch;
import io.ikws4.weiju.R;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.page.BaseFragment;
import io.ikws4.weiju.page.editor.view.EditorSymbolBar;
import io.ikws4.weiju.page.home.HomeViewModel;
import io.ikws4.weiju.page.home.widget.ScriptListView;

public class EditorFragment extends BaseFragment {
    private Editor vEditor;
    private HomeViewModel vm;
    private ScriptListView.ScriptItem mItem;

    public EditorFragment() {
        super(R.layout.editor_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        mItem = requireArguments().getParcelable("item");

        vEditor = view.findViewById(R.id.editor);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) vEditor.getLayoutParams();
        layoutParams.leftMargin = (int) vEditor.getCharWidth();
        vEditor.setLayoutParams(layoutParams);
        vEditor.setText(mItem.script);
        vEditor.setSelection(0, 0);
        vEditor.moveSelectionEnd();
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(vEditor, 0);

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
        super.onCreateMenu(menu, menuInflater);

        menuInflater.inflate(R.menu.editor_menu, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.help) {
            Toast.makeText(getContext(), "TODO: Help", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.close) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(vEditor.getWindowToken(), 0);
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        var item = ScriptListView.ScriptItem.from(vEditor.getText().toString());
        if (hasSameMetadataInMyScripts(item)) {
            Toast.makeText(getContext(), "ABORT: Same metadata already exist.", Toast.LENGTH_SHORT).show();
            return;
        } else if (hasMetadataError(item)) {
            Toast.makeText(getContext(), "ABORT: Metadata parse error.", Toast.LENGTH_SHORT).show();
            return;
        }

        vm.replaceInMyScripts(mItem, item);
    }

    private boolean hasMetadataError(ScriptListView.ScriptItem item) {
        return item == ScriptListView.ScriptItem.EMPTY_ITEM;
    }

    private boolean hasSameMetadataInMyScripts(ScriptListView.ScriptItem item) {
        for (var it : vm.getMyScripts().getValue()) {
            if (it.metadataEquals(mItem)) continue;
            if (it.metadataEquals(item)) return true;
        }
        return false;
    }
}
