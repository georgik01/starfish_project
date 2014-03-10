package com.example.starfishapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

class ImgCache {
  final Map<String, Bitmap> cache = new HashMap<String, Bitmap>();

  public void bind(String url, ImageView view) {
      ImgDownload download = new ImgDownload(url, view);
      download.execute();
  }

  class ImgDownload extends AsyncTask<Object, Void, Bitmap> {
    final private String requestUrl;
    final private ImageView view;
//    private Bitmap pic;

    ImgDownload(String requestUrl, ImageView view) {
      this.requestUrl = requestUrl;
      this.view = view;
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
      Bitmap bm = cache.get(requestUrl);
      if (bm == null) {
        try {
          URL url = new URL(requestUrl);
          URLConnection conn = url.openConnection();
          return BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      return bm;
    }

    @Override
    protected void onPostExecute(Bitmap pic) {
      view.setImageBitmap(pic);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      view.setLayoutParams(layoutParams);
      cache.put(requestUrl, pic);
    }
  }
}
