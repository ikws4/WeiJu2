package io.ikws4.codeeditor.ext;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;

public class OutlineColorDrawable extends ColorDrawable {
  public final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public OutlineColorDrawable(int outlineColor, int fillColor) {
    super(fillColor);

    // 4dp
    int width = (int) (3 * Resources.getSystem().getDisplayMetrics().density);
    paint.setColor(outlineColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(width);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    canvas.drawRect(getBounds(), paint);
  }
}
