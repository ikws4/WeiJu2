package io.ikws4.weiju.editor;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import io.github.rosemoe.sora.graphics.GraphicsConstants;
import io.github.rosemoe.sora.lang.styling.TextStyle;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

class EditorRenderer extends io.github.rosemoe.sora.widget.EditorRenderer {
    private final RectF tmpRect;
    private final Path tmpPath;
    private final CodeEditor editor;

    public EditorRenderer(@NonNull CodeEditor editor) {
        super(editor);
        this.editor = editor;
        tmpRect = new RectF();
        tmpPath = new Path();
    }


    @Override
    protected void patchTextRegionWithColor(Canvas canvas, float textOffset, int start, int end, int color, int backgroundColor) {
        paintGeneral.setColor(color);
        var underlineColor = editor.getColorScheme().getColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_UNDERLINE);
        paintOther.setStrokeWidth(editor.getRowHeightOfText() * 0.1f);
        paintGeneral.setStyle(android.graphics.Paint.Style.FILL_AND_STROKE);
        // Change: Remove bold style
        // paintGeneral.setFakeBoldText(true);
        patchTextRegions(canvas, textOffset, getTextRegionPositions(start, end), (canvasLocal, horizontalOffset, row, line, startCol, endCol, style) -> {
            if (backgroundColor != 0) {
                tmpRect.top = getRowTopForBackground(row) - editor.getOffsetY();
                tmpRect.bottom = getRowBottomForBackground(row) - editor.getOffsetY();
                tmpRect.left = 0;
                tmpRect.right = editor.getWidth();
                paintOther.setColor(backgroundColor);
                if (editor.getProps().enableRoundTextBackground) {
                    canvas.drawRoundRect(tmpRect, editor.getRowHeight() * editor.getProps().roundTextBackgroundFactor, editor.getRowHeight() * editor.getProps().roundTextBackgroundFactor, paintOther);
                } else {
                    canvas.drawRect(tmpRect, paintOther);
                }
            }
            if (color != 0) {
                paintGeneral.setTextSkewX(TextStyle.isItalics(style) ? GraphicsConstants.TEXT_SKEW_X : 0f);
                paintGeneral.setStrikeThruText(TextStyle.isStrikeThrough(style));
                drawText(canvas, getLine(line), startCol, endCol - startCol, startCol, endCol - startCol, false, horizontalOffset, editor.getRowBaseline(row) - editor.getOffsetY(), line);
            }
            if (underlineColor != 0) {
                paintOther.setColor(underlineColor);
                var bottom = editor.getRowBottomOfText(row) - editor.getOffsetY() - editor.getRowHeightOfText() * 0.05f;
                canvas.drawLine(0, bottom, editor.getWidth(), bottom, paintOther);
            }
        });
        paintGeneral.setStyle(android.graphics.Paint.Style.FILL);
        paintGeneral.setFakeBoldText(false);
        paintGeneral.setTextSkewX(0f);
        paintGeneral.setStrikeThruText(false);
    }
}
