package io.ikws4.weiju.editor;

import android.graphics.drawable.GradientDrawable;
import android.view.HapticFeedbackConstants;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.LongPressEvent;
import io.github.rosemoe.sora.event.Unsubscribe;
import io.github.rosemoe.sora.util.IntPair;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;

class EditorActionWindow extends EditorTextActionWindow {

    public EditorActionWindow(@NonNull CodeEditor editor) {
        super(editor);
        ViewGroup root = getView();

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5 * editor.getDpUnit());
        gd.setStroke(1, RosepineColorScheme.HIGHLIGHT_LOW);
        gd.setColor(RosepineColorScheme.SURFACE);
        root.setBackground(gd);

        ImageButton selectAll = root.findViewById(io.github.rosemoe.sora.R.id.panel_btn_select_all);
        ImageButton cut = root.findViewById(io.github.rosemoe.sora.R.id.panel_btn_cut);
        ImageButton copy = root.findViewById(io.github.rosemoe.sora.R.id.panel_btn_copy);
        ImageButton paste = root.findViewById(io.github.rosemoe.sora.R.id.panel_btn_paste);
        selectAll.getDrawable().setTint(RosepineColorScheme.TEXT);
        cut.getDrawable().setTint(RosepineColorScheme.TEXT);
        copy.getDrawable().setTint(RosepineColorScheme.TEXT);
        paste.getDrawable().setTint(RosepineColorScheme.TEXT);

        editor.subscribeEvent(LongPressEvent.class, new EventReceiver<LongPressEvent>() {
            @Override
            public void onReceive(LongPressEvent event, Unsubscribe unsubscribe) {
                if (editor.getCursor().isSelected()) {
                    long res = editor.getPointPositionOnScreen(event.getX(), event.getY());
                    int line = IntPair.getFirst(res);
                    int column = IntPair.getSecond(res);
                    editor.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    editor.selectWord(line, column);
                }
            }
        });
    }
}
