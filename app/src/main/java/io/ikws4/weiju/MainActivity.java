package io.ikws4.weiju;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import io.ikws4.codeeditor.CodeEditor;
import io.ikws4.codeeditor.api.document.Document;
import io.ikws4.weiju.storage.ScriptStore;
import io.ikws4.weiju.view.AppListView;

public class MainActivity extends AppCompatActivity {
    private CodeEditor mEditor;
    private Toolbar mToolbar;
    private AppListView mAppList;

    private boolean mZenMode;

    private ScriptStore mStorage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorage = ScriptStore.getInstance(this);

        Globals globals = JsePlatform.standardGlobals();

        mEditor = findViewById(R.id.code_editor);
        mToolbar = findViewById(R.id.toolbar);
        mAppList = findViewById(R.id.rv_app_list);

        mToolbar.setOnMenuItemClickListener((menu) -> {
            if (menu.getItemId() == R.id.menu_run) {
                LuaValue chunk = globals.load(mEditor.getDocument().toString());
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
                mStorage.put(mAppList.getSelectedPkg().toString(), mEditor.getDocument().toString());
            }
            return false;
        });

        mAppList.setOnItemClickListener((pkg) -> {
            mEditor.setDocument(new Document(mStorage.get(pkg.toString())));
        });

        mEditor.setDocument(new Document(mStorage.get(mAppList.getSelectedPkg().toString())));
    }
}
