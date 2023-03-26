package io.ikws4.weiju.utils;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;

public class FileUtility {

   public static String readAssetFile(Context context, String fileName) {
      String fileContent = null;
      try {
         InputStream inputStream = context.getAssets().open(fileName);
         int size = inputStream.available();
         byte[] buffer = new byte[size];
         inputStream.read(buffer);
         inputStream.close();
         fileContent = new String(buffer, "UTF-8");
      } catch (IOException e) {
         e.printStackTrace();
      }
      return fileContent;
   }
}
