package io.ikws4.weiju.page;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import io.ikws4.weiju.viewmodel.AppListViewModel;
import io.ikws4.weiju.widget.dialog.SearchBar.SearchBar;
import io.ikws4.weiju.widget.dialog.SearchBar.SelectedAppInfoItemLoader;
import io.ikws4.weiju.widget.view.AppListView;

public class MainActivity extends AppCompatActivity {
    // For xposed to hook this variable to indicate
    // that xposed works.
    private static boolean XPOSED_ENABLED = false;

    private Editor mEditor;
    private Toolbar mToolbar;
    private AppListView mAppList;

    private boolean mZenMode;

    private ScriptStore mStorage;
    private Preferences mPreferences;

    private AppListViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mViewModel = new ViewModelProvider(this).get(AppListViewModel.class);

        mStorage = ScriptStore.getInstance(this);
        mPreferences = Preferences.getInstance(this);

        Globals globals = JsePlatform.standardGlobals();

        mEditor = findViewById(R.id.code_editor);
        mToolbar = findViewById(R.id.toolbar);
        mAppList = findViewById(R.id.rv_item_list);

        mToolbar.setOnMenuItemClickListener((menu) -> {
            if (menu.getItemId() == R.id.menu_run) {
                // LuaValue chunk = globals.load(mEditor.getText().toString());
                // LuaValue result = chunk.call();
                // Toast.makeText(this, result + "", Toast.LENGTH_SHORT).show();
                return true;
            } else if (menu.getItemId() == R.id.menu_toggle_zem_mode) {
                if (mZenMode) {
                    menu.setIcon(R.drawable.ic_fullscreen);
                    mAppList.setVisibility(View.VISIBLE);
                } else {
                    menu.setIcon(R.drawable.ic_fullscreen_exit);
                    mAppList.setVisibility(View.GONE);
                }
                mZenMode = !mZenMode;
            } else if (menu.getItemId() == R.id.menu_save) {
                // mStorage.put(mPreferences.get(Preferences.APP_LIST_SELECTED_PACKAGE, ""), mEditor.getText().toString());
            } else if (menu.getItemId() == R.id.menu_xposed_status) {
                Toast.makeText(this, "WeiJu was not enabled in xposed.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        mToolbar.getMenu().findItem(R.id.menu_xposed_status).setVisible(!XPOSED_ENABLED);

        mAppList.setOnItemClickListener(pkg -> {
            // mEditor.setText(mStorage.get(pkg));
        });

        SearchBar searchBar = new SearchBar(this, new SelectedAppInfoItemLoader());
        searchBar.setOnItemClickListener(item -> {
            mViewModel.selectApp((AppInfo) item.userData);
            mAppList.scrollToBottom();
            return true;
        });

        mAppList.setOnAddAppClickListener(v -> {
            searchBar.show();
        });

        mViewModel.getSelectedAppInfos().observe(this, infos -> {
            mAppList.setData(infos);
            mAppList.scrollToSelectedPkgPosition();
            // mEditor.setText(mStorage.get(mAppList.getSelectedPkg()));
        });
    }
}
