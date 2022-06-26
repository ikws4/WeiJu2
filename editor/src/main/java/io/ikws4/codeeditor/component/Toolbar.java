package io.ikws4.codeeditor.component;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import io.ikws4.codeeditor.R;
import io.ikws4.codeeditor.api.editor.Editor;
import io.ikws4.codeeditor.api.editor.component.Component;
import io.ikws4.codeeditor.api.editor.listener.VisibleAreaListener;
import io.ikws4.codeeditor.widget.KeyButton;

/**
 * Provide such a arrow keys, and Tab key.
 */
public class Toolbar extends FrameLayout implements Component, VisibleAreaListener {

    private KeyButton mKeyboardToggleButton;
    private boolean mKeyboardShowing;
    private int mEditorHeight;

    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.toolbar, this, true);
    }

    @Override
    public void onAttachEditor(Editor editor) {
        setBackgroundColor(editor.getColorScheme().ui.background);

        KeyButton tab = findViewById(R.id.tab);
        KeyButton up = findViewById(R.id.up);
        KeyButton down = findViewById(R.id.down);
        KeyButton left = findViewById(R.id.left);
        KeyButton right = findViewById(R.id.right);
        mKeyboardToggleButton = findViewById(R.id.keyboard_toggle);

        up.setOnPressedListener(editor.getSelectionModel()::moveUp);
        down.setOnPressedListener(editor.getSelectionModel()::moveDown);
        left.setOnPressedListener(editor.getSelectionModel()::moveLeft);
        right.setOnPressedListener(editor.getSelectionModel()::moveRight);
        mKeyboardToggleButton.setOnPressedListener(() -> {
            if (mKeyboardShowing) {
                editor.hideSoftInput();
            } else {
                editor.showSoftInput();
            }
        });

        editor.getScrollingModel().addVisibleAreaListener(this);
    }

    @Override
    public int getComponentWidth() {
        return getWidth();
    }

    @Override
    public int getComponentHeight() {
        return getHeight();
    }

    @Override
    public void onVisibleAreaChanged(Rect rect, Rect oldRect) {
        int diff = mEditorHeight - rect.height();
        if (Math.abs(diff) > 100) {
            boolean showing = diff > 100;
            if (showing) {
                mKeyboardShowing = true;
                mKeyboardToggleButton.setImageResource(R.drawable.ic_keyboard_hide);
            } else {
                mKeyboardShowing = false;
                mKeyboardToggleButton.setImageResource(R.drawable.ic_keyboard);
            }
            mEditorHeight = rect.height();
        }
    }
}
