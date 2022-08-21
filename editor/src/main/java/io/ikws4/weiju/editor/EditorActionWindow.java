package io.ikws4.weiju.editor;

import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;

class EditorActionWindow extends EditorTextActionWindow {
    private CodeEditor mEditor;
    private SelectionChangeEvent mPrevSelectionChangedEvent;

    private static final int INSERT_HANDLE = 0;
    private static final int LEFT_HANDLE = 1;
    private static final int RIGHT_HANDLE = 2;
    private int movedHandle = INSERT_HANDLE;

    public EditorActionWindow(@NonNull CodeEditor editor) {
        super(editor);
        mEditor = editor;
        ViewGroup root = getView();

        setSize(0, (int) (48 * editor.getDpUnit()));
        GradientDrawable gd = new GradientDrawable();
        gd.setStroke(1, RosepineColorScheme.HIGHLIGHT_LOW);
        gd.setColor(RosepineColorScheme.SURFACE);
        root.setBackground(gd);
    }

    @Override
    public int getHeight() {
        return getView().getMeasuredHeight();
    }

    @Override
    public void onReceive(SelectionChangeEvent event, Unsubscribe unsubscribe) {
        if (event.getCause() == SelectionChangeEvent.CAUSE_SELECTION_HANDLE) {
            if (mPrevSelectionChangedEvent != null) {
                if (mPrevSelectionChangedEvent.getLeft().equals(event.getLeft())) {
                    movedHandle = RIGHT_HANDLE;
                } else {
                    movedHandle = LEFT_HANDLE;
                }
            }
        } else {
            movedHandle = INSERT_HANDLE;
        }
        mPrevSelectionChangedEvent = event;
        super.onReceive(event, unsubscribe);
    }

    @Override
    public void displayWindow() {
        float panelX, panelY;
        var leftRect = mEditor.getLeftHandleDescriptor().position;
        var rightRect = mEditor.getRightHandleDescriptor().position;

        if (movedHandle == LEFT_HANDLE) {
            panelY = selectTop(leftRect);
            panelX = mEditor.getOffset(mEditor.getCursor().getLeftLine(), mEditor.getCursor().getLeftColumn());
        } else {
            panelY = selectTop(rightRect);
            panelX = mEditor.getOffset(mEditor.getCursor().getRightLine(), mEditor.getCursor().getRightColumn());
        }
        panelY = Math.max(0, Math.min(panelY, mEditor.getHeight() - getHeight() - 5));
        panelX -= getWidth() / 2f;

        setLocationAbsolutely((int) panelX, (int) panelY);
        show();
    }

    private int selectTop(RectF rect) {
        var rowHeight = mEditor.getRowHeight();
        if (rect.top - rowHeight * 3 / 2F > getHeight()) {
            return (int) (rect.top - rowHeight * 3 / 2 - getHeight());
        } else {
            return (int) (rect.bottom + rowHeight / 2);
        }
    }

    @Override
    public void dismiss() {
        getView().postDelayed(super::dismiss, 250);
    }
}
