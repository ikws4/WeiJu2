package io.ikws4.weiju.util;

import android.content.res.Resources;

public class UnitConverter {
  public static int dp(int px) {
    return (int) (px * Resources.getSystem().getDisplayMetrics().density);
  }
}
