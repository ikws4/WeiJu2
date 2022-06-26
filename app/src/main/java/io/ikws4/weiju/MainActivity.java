package io.ikws4.weiju;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import io.ikws4.codeeditor.CodeEditor;

public class MainActivity extends AppCompatActivity {
    private CodeEditor mEditor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Globals globals = JsePlatform.standardGlobals();

        mEditor = findViewById(R.id.code_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener((menu) -> {
            if (menu.getItemId() == R.id.menu_run) {
                LuaValue chunk = globals.load(mEditor.getDocument().toString());
                LuaValue result = chunk.call();
                Toast.makeText(this, result + "", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}
