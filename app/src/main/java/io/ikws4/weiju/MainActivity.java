package io.ikws4.weiju;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.ikws4.weiju.storage.ScriptStore;
import io.ikws4.weiju.view.AppListView;
import io.ikws4.weiju.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    // For xposed to hook this variable to indicate
    // that xposed works.
    private static boolean XPOSED_ENABLED = false;

    private CodeEditor mEditor;
    private Toolbar mToolbar;
    private AppListView mAppList;

    private boolean mZenMode;

    private ScriptStore mStorage;

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mStorage = ScriptStore.getInstance(this);

        Globals globals = JsePlatform.standardGlobals();

        mEditor = findViewById(R.id.code_editor);
        mToolbar = findViewById(R.id.toolbar);
        mAppList = findViewById(R.id.rv_app_list);

        mToolbar.setOnMenuItemClickListener((menu) -> {
            if (menu.getItemId() == R.id.menu_run) {
                LuaValue chunk = globals.load(mEditor.getText().toString());
                LuaValue result = chunk.call();
                Toast.makeText(this, result + "", Toast.LENGTH_SHORT).show();
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
                mStorage.put(mAppList.getSelectedPkg().toString(), mEditor.getText().toString());
            } else if (menu.getItemId() == R.id.menu_xposed_status) {
                Toast.makeText(this, "WeiJu was not enabled in xposed.", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        mToolbar.getMenu().findItem(R.id.menu_xposed_status).setVisible(!XPOSED_ENABLED);

        mAppList.setOnItemClickListener((pkg) -> {
            mEditor.setText(mStorage.get(pkg.toString()));
        });

        mViewModel.getAppInfos().observe(this, (infos) -> {
            mAppList.addData(infos);
            mEditor.setText(mStorage.get(mAppList.getSelectedPkg().toString()));
        });
    }

}
