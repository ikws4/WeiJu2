package io.ikws4.weiju.page.home.widget;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

import io.ikws4.weiju.R;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.widget.Dialog;

public class CreateScriptDialog extends Dialog {
    private EditText vName, vAuthor, vDescription;
    private Spinner vTemplate;
    private OnCreateListener mCreateListener;

    public CreateScriptDialog(Context context) {
        super(context, R.layout.create_script_dialog);
        vName = findViewById(R.id.v_name);
        vAuthor = findViewById(R.id.v_author);
        vDescription = findViewById(R.id.v_description);
        vTemplate = findViewById(R.id.v_template);

        vAuthor.setText(Preferences.getInstance(context).get(Preferences.AUTHOR, ""));

        Button closeButton = findViewById(R.id.v_close);
        Button createButton = findViewById(R.id.v_create);

        closeButton.setOnClickListener((v) -> dismiss());
        createButton.setOnClickListener((v) -> {
            if (mCreateListener != null) mCreateListener.onCreate(this);
            Preferences.getInstance(context).put(Preferences.AUTHOR, vAuthor.getText().toString());
            dismiss();
        });
    }

    public CreateScriptDialog setOnCreateListener(OnCreateListener l) {
        mCreateListener = l;
        return this;
    }

    public String getName() {
        return vName.getText().toString();
    }

    public String getAuthor() {
        return vAuthor.getText().toString();
    }

    public String getDescription() {
        return vDescription.getText().toString();
    }

    public String getTemplate() {
        return vTemplate.getSelectedItem().toString().toLowerCase(Locale.ROOT);
    }

    public interface OnCreateListener {
        void onCreate(CreateScriptDialog dialog);
    }
}
