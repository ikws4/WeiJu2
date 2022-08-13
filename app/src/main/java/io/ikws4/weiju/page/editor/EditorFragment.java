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
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.TimeUnit;

import io.ikws4.weiju.R;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.page.BaseFragment;
import io.ikws4.weiju.page.MainViewModel;
import io.ikws4.weiju.page.editor.view.EditorSymbolBar;
import io.ikws4.weiju.page.home.HomeViewModel;
import io.ikws4.weiju.page.home.widget.ScriptListView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class EditorFragment extends BaseFragment {
    private Editor vEditor;
    private HomeViewModel vm;
    private ScriptListView.ScriptItem mItem;
    private CompositeDisposable mCompositeDisposable;

    public EditorFragment() {
        super(R.layout.editor_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mCompositeDisposable = new CompositeDisposable();

        mItem = requireArguments().getParcelable("item");

        vEditor = view.findViewById(R.id.editor);
        EditorSymbolBar vSymbolBar = view.findViewById(R.id.editor_symbol_bar);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) vEditor.getLayoutParams();
        layoutParams.leftMargin = (int) vEditor.getCharWidth();
        vEditor.setLayoutParams(layoutParams);
        vEditor.setText(mItem.script);
        if (mItem.isPackage) {
            vEditor.setEditable(false);
            vSymbolBar.setVisibility(View.GONE);
        }
        vSymbolBar.attach(vEditor);

        // Auto Save
        MainViewModel mainVM = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (!mItem.isPackage) {
            mCompositeDisposable.add(Observable.timer(15, TimeUnit.SECONDS)
                .repeat()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((it) -> {
                    mainVM.showProgressBar();
                    trySave(false);
                    mCompositeDisposable.add(Completable.timer(2, TimeUnit.SECONDS)
                        .subscribe(mainVM::hideProgressBar));
                }));
        }
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
            if (trySave(true)) {
                requireActivity().onBackPressed();
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        trySave(true);
    }

    private boolean trySave(boolean toast) {
        // Don't need perform save becuase it can not be change
        if (mItem.isPackage) return true;

        var item = ScriptListView.ScriptItem.from(vEditor.getText().toString());
        if (item.equals(mItem)) return true;

        if (hasSameMetadataInMyScripts(item)) {
            if (toast) Toast.makeText(getContext(), "ABORT: Same metadata already exist.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (hasMetadataError(item)) {
            if (toast) Toast.makeText(getContext(), "ABORT: Metadata parse error.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String msg = item.verify();
            if (!msg.isEmpty()) {
                if (toast) Toast.makeText(getContext(), "ABORT: " + msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        vm.replaceInMyScripts(mItem, item);
        mItem = item;
        return true;
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
