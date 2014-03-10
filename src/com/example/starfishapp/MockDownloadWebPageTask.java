package com.example.starfishapp;

import android.app.Activity;
import android.util.Log;

import java.io.*;

class MockDownloadWebPageTask extends DownloadWebPageTask {

  final private Activity mainActivity;

  public MockDownloadWebPageTask(Activity mainActivity) {
    this.mainActivity = mainActivity;
  }

  @Override
  protected String execute(String... strings) {
    String result = "";
    for(String url:strings){
      try {
        InputStream is = mainActivity.openFileInput(url + ".JSON");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = br.readLine()) != null) {
          result += s;
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        Log.e("MockDownloader", e.getMessage());
      } catch (IOException e) {
        e.printStackTrace();
        Log.e("MockDownloader", e.getMessage());
      }
    }
    return result;
  }
}
