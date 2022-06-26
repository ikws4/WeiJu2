package io.ikws4.codeeditor.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import io.ikws4.codeeditor.api.configuration.ColorScheme;
import io.ikws4.codeeditor.api.editor.Editor;
import io.ikws4.codeeditor.api.editor.LayoutModel;
import io.ikws4.codeeditor.api.editor.component.Component;
import io.ikws4.codeeditor.api.editor.listener.ScaleListener;
import io.ikws4.codeeditor.api.editor.listener.VisibleAreaListener;

public class Gutter extends View implements Component, VisibleAreaListener, ScaleListener {
  private Editor mEditor;
  private int mScrollY;
  private final Paint mTextPaint = new Paint();
  private final Paint mActiveTextPaint = new Paint();

  public Gutter(Context context) {
    this(context, null);
  }

  public Gutter(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public Gutter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void onAttachEditor(Editor editor) {
    mEditor = editor;

    ColorScheme colorScheme = editor.getConfiguration().colorScheme;

    setBackgroundColor(colorScheme.ui.gutterBackground);
    setTextSize(mEditor.getConfiguration().fontSize * mEditor.getScacleModel().getScaleFactor());

    mTextPaint.setColor(colorScheme.ui.gutterForeground);
    mTextPaint.setTypeface(Typeface.MONOSPACE);
    mTextPaint.setAntiAlias(true);
    mTextPaint.setTextAlign(Paint.Align.RIGHT);

    mActiveTextPaint.setColor(colorScheme.ui.gutterActivedForeground);
    mActiveTextPaint.setTypeface(Typeface.MONOSPACE);
    mActiveTextPaint.setAntiAlias(true);
    mActiveTextPaint.setTextAlign(Paint.Align.RIGHT);

    editor.getScrollingModel().addVisibleAreaListener(this);
    editor.getScacleModel().addScaleListener(this);
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
    mScrollY = rect.top;
  }

  @Override
  public void onScaleChanged(float factor) {
    setTextSize(mEditor.getConfiguration().fontSize * factor);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int w = View.resolveSize(getWidth(), widthMeasureSpec);
    int h = View.resolveSize(getHeight(), heightMeasureSpec);
    setMeasuredDimension(w, h);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (mEditor.getConfiguration().number) {
      measureGutterWidth();
      drawLineNumber(canvas);
    }
  }

  private void drawLineNumber(Canvas canvas) {
    LayoutModel layout = mEditor.getLayoutModel();
    int topLine = layout.getTopLine();
    int bottomLine = layout.getBottomLine();
    int currentLine = layout.getCurrentLine();

    for (int line = topLine; line <= bottomLine; line++) {
      String realLine = String.valueOf(line + 1);
      float x = getWidth() - getPaddingRight();
      float y = mEditor.getLayoutModel().getLineBaseline(line) - mScrollY;
      canvas.drawText(realLine, x, y, line == currentLine && !mEditor.isViwer() ? mActiveTextPaint : mTextPaint);
    }
  }

  private void measureGutterWidth() {
    int lineCount = mEditor.getLayoutModel().getLineCount();
    String lineCountText = String.valueOf(lineCount);

    // When the number digits are not equal, we need relayout.
    // eg. when linenumber from 99 become to 100.
    if (Math.log10(lineCount) != Math.log10(lineCount - 1)) {
      requestLayout();
    }

    getLayoutParams().width = (int) (mTextPaint.measureText(lineCountText));
    getLayoutParams().width += getPaddingLeft() + getPaddingRight();
  }

  private void setTextSize(float size) {
    float s = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size, getResources().getDisplayMetrics());
    mTextPaint.setTextSize(s);
    mActiveTextPaint.setTextSize(s);
  }
}
