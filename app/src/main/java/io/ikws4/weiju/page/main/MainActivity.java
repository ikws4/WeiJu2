package io.ikws4.weiju.page.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

import io.ikws4.weiju.R;
import io.ikws4.weiju.data.AppInfo;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.storage.ScriptStore;
import io.ikws4.weiju.widget.dialog.searchbar.SearchBar;
import io.ikws4.weiju.widget.dialog.searchbar.SelectedAppInfoItemLoader;
import io.ikws4.weiju.widget.view.AppListView;
import io.ikws4.weiju.widget.view.ScriptListView;

public class MainActivity extends AppCompatActivity {
    // For xposed to hook this variable to indicate
    // that xposed works.
    private static boolean XPOSED_ENABLED = false;

    private Editor vEditor;
    private Toolbar vToolbar;
    private AppListView vAppList;
    private ScriptListView vScripts;
    // private ScriptListView vAvaliableScripts;
    private ViewFlipper vMyScriptsViewFlipper;
    private ViewFlipper vAvailableScriptsViewFlipper;

    private boolean mZenMode;

    private ScriptStore mStorage;
    private Preferences mPreferences;

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mStorage = ScriptStore.getInstance(this);
        mPreferences = Preferences.getInstance(this);

        Globals globals = JsePlatform.standardGlobals();

        // mEditor = findViewById(R.id.code_editor);
        vToolbar = findViewById(R.id.toolbar);
        vAppList = findViewById(R.id.rv_item_list);
        vScripts = findViewById(R.id.rv_scripts);
        // vMyScripts = findViewById(R.id.rv_my_scripts);
        // vAvaliableScripts = findViewById(R.id.rv_avaliable_scripts);
        // vMyScriptsViewFlipper = findViewById(R.id.vf_my_scripts);
        // vAvailableScriptsViewFlipper = findViewById(R.id.vf_available_scripts);

        vToolbar.setOnMenuItemClickListener((menu) -> {
            if (menu.getItemId() == R.id.menu_run) {
                // LuaValue chunk = globals.load(mEditor.getText().toString());
                // LuaValue result = chunk.call();
                // Toast.makeText(this, result + "", Toast.LENGTH_SHORT).show();
                return true;
            } else if (menu.getItemId() == R.id.menu_toggle_zem_mode) {
                if (mZenMode) {
                    menu.setIcon(R.drawable.ic_fullscreen);
                    vAppList.setVisibility(View.VISIBLE);
                } else {
                    menu.setIcon(R.drawable.ic_fullscreen_exit);
                    vAppList.setVisibility(View.GONE);
                }
                mZenMode = !mZenMode;
            } else if (menu.getItemId() == R.id.menu_save) {
                // mStorage.put(mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, ""), mEditor.getText().toString());
            } else if (menu.getItemId() == R.id.menu_xposed_status) {
                Toast.makeText(this, "WeiJu was not enabled in xposed.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        vToolbar.getMenu().findItem(R.id.menu_xposed_status).setVisible(!XPOSED_ENABLED);


        vScripts.registerCallbacks(new ScriptListView.Callbacks() {
            @Override
            public void onAddToMyScripts(View v, ScriptListView.ScriptItem item) {
                mViewModel.removeFromAvaliableScripts(item);
                mViewModel.addToMyScript(item);
            }

            @Override
            public void onRemoveFromMyScripts(View v, ScriptListView.ScriptItem item) {
                mViewModel.removeFromMyScripts(item);
            }
        });
        // Drag script from avaliable scripts to my scripts
        // vAvaliableScripts.setItemLongClickListener((v, x, y, item) -> {
        //     ClipData.Item clipDataItem = new ClipData.Item(item.name);
        //     ClipData data = new ClipData(item.name, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, clipDataItem);
        //     View.DragShadowBuilder shadow = new View.DragShadowBuilder(v) {
        //         @Override
        //         public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        //             outShadowSize.set(v.getWidth(), v.getHeight());
        //             outShadowTouchPoint.set((int) x, (int) y);
        //         }
        //     };
        //     v.startDragAndDrop(data, shadow, new Pair<>(v, item), 0);
        //     v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        // });
        //
        // vMyScriptsViewFlipper.setOnDragListener((v, event) -> {
        //     Pair<View, ScriptListView.ScriptItem> data = (Pair<View, ScriptListView.ScriptItem>) event.getLocalState();
        //     View dragView = data.first;
        //     ScriptListView.ScriptItem item = data.second;
        //     switch (event.getAction()) {
        //         case DragEvent.ACTION_DRAG_STARTED:
        //             mViewModel.removeFromAvaliableScripts(item);
        //             vMyScriptsViewFlipper.setForeground(AppCompatResources.getDrawable(this, R.drawable.script_drag_target_foreground));
        //             return true;
        //         case DragEvent.ACTION_DRAG_ENTERED:
        //             vMyScriptsViewFlipper.setForeground(AppCompatResources.getDrawable(this, R.drawable.script_drag_target_foreground_outlined));
        //             return true;
        //         case DragEvent.ACTION_DROP:
        //             mViewModel.addToMyScript(item);
        //             vMyScriptsViewFlipper.setForeground(null);
        //             return true;
        //         case DragEvent.ACTION_DRAG_ENDED:
        //             if (event.getResult() == false) {
        //                 mViewModel.addToAvaliableScripts(item);
        //             }
        //             vMyScriptsViewFlipper.setForeground(null);
        //             return true;
        //         case DragEvent.ACTION_DRAG_EXITED:
        //         case DragEvent.ACTION_DRAG_LOCATION:
        //             return true;
        //     }
        //
        //     return false;
        // });

        SearchBar searchBar = new SearchBar(this, new SelectedAppInfoItemLoader());
        searchBar.setOnItemClickListener(item -> {
            mViewModel.selectApp((AppInfo) item.userData);
            vAppList.scrollToBottom();
            return true;
        });

        vAppList.setOnItemClickListener(app -> {
            // mEditor.setText(mStorage.get(pkg));
            mViewModel.switchApp(app.pkg);
        });

        vAppList.setOnAddAppClickListener(v -> {
            searchBar.show();
        });

        mViewModel.getSelectedAppInfos().observe(this, infos -> {
            vAppList.setData(infos);
            vAppList.scrollToSelectedPkgPosition();
            // mEditor.setText(mStorage.get(mAppList.getSelectedPkg()));
        });

        mViewModel.getAvaliableScripts().observe(this, scripts -> {
            vScripts.setData(mViewModel.getMyScripts().getValue(), scripts);
        });

        mViewModel.getMyScripts().observe(this, scripts -> {
            vScripts.setData(scripts, mViewModel.getAvaliableScripts().getValue());
        });

        // mViewModel.getAvaliableScripts().observe(this, scripts -> {
        //     if (scripts == null) {
        //         // loading
        //         vAvailableScriptsViewFlipper.setDisplayedChild(0);
        //     } else if (scripts.isEmpty()) {
        //         vAvailableScriptsViewFlipper.setDisplayedChild(1);
        //         vAvaliableScripts.setData(scripts);
        //     } else {
        //         vAvaliableScripts.setData(scripts);
        //         vAvailableScriptsViewFlipper.setDisplayedChild(2);
        //     }
        // });
        //
        // mViewModel.getMyScripts().observe(this, scripts -> {
        //     if (scripts.isEmpty()) {
        //         vMyScriptsViewFlipper.setDisplayedChild(0);
        //         vMyScripts.setData(scripts);
        //     } else {
        //         vMyScripts.setData(scripts);
        //         vMyScriptsViewFlipper.setDisplayedChild(1);
        //     }
        // });
    }
}
