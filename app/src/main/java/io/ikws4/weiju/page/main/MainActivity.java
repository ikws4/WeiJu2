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
import io.ikws4.weiju.widget.dialog.ScriptListView;
import io.ikws4.weiju.widget.dialog.searchbar.SearchBar;
import io.ikws4.weiju.widget.dialog.searchbar.SelectedAppInfoItemLoader;
import io.ikws4.weiju.widget.view.AppListView;

public class MainActivity extends AppCompatActivity {
    // For xposed to hook this variable to indicate
    // that xposed works.
    private static boolean XPOSED_ENABLED = false;

    private Editor vEditor;
    private Toolbar vToolbar;
    private AppListView vAppList;
    private ScriptListView vMyScripts;
    private ScriptListView vAvaliableScripts;
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
        vMyScripts = findViewById(R.id.rv_my_scripts);
        vAvaliableScripts = findViewById(R.id.rv_avaliable_scripts);
        vMyScriptsViewFlipper = findViewById(R.id.vf_my_scripts);
        vAvailableScriptsViewFlipper = findViewById(R.id.vf_available_scripts);

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

        SearchBar searchBar = new SearchBar(this, new SelectedAppInfoItemLoader());
        searchBar.setOnItemClickListener(item -> {
            mViewModel.selectApp((AppInfo) item.userData);
            vAppList.scrollToBottom();
            return true;
        });

        vAppList.setOnItemClickListener(pkg -> {
            // mEditor.setText(mStorage.get(pkg));
            mViewModel.switchApp(pkg);
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
            if (scripts == null) {
                // loading
                vAvailableScriptsViewFlipper.setDisplayedChild(0);
            } else if (scripts.isEmpty()) {
                vAvailableScriptsViewFlipper.setDisplayedChild(1);
                vAvaliableScripts.setData(scripts);
            } else {
                vAvaliableScripts.setData(scripts);
                vAvailableScriptsViewFlipper.setDisplayedChild(2);
            }
        });
    }
}
