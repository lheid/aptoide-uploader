package com.aptoide.uploader.account;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import io.reactivex.Single;

public class AutoLoginManager {
  private static Context context;
  private AutoLoginCredentials autoLoginCredentials = new AutoLoginCredentials();
  private static AutoLoginManager single_instance;

  public AutoLoginManager(Context context) {
    this.context = context;
  }

  public static AutoLoginManager getInstance(Context context) {
    if (single_instance == null) single_instance = new AutoLoginManager(context);
    return single_instance;
  }

  public AutoLoginCredentials getAutoLoginCredentials() {
    return autoLoginCredentials;
  }

  public Single<AutoLoginCredentials> getStoredUserCredentials() {
    return Single.just(autoLoginCredentials);
  }

  public Single<AutoLoginCredentials> getFirstStoredUserCredentials() {
    try {
      String URL = "content://cm.aptoide.pt.StubProvider";
      Uri token_uri = Uri.parse(URL + "/token");
      Uri refresh_token_uri = Uri.parse(URL + "/refreshToken");
      Uri repo_uri = Uri.parse(URL + "/repo");
      Uri email_uri = Uri.parse(URL + "/loginName");

      Cursor c1 = context.getContentResolver()
          .query(token_uri, null, null, null, null);
      Cursor c2 = context.getContentResolver()
          .query(refresh_token_uri, null, null, null, null);
      Cursor c3 = context.getContentResolver()
          .query(repo_uri, null, null, null, null);
      Cursor c4 = context.getContentResolver()
          .query(email_uri, null, null, null, null);

      if (c1 != null && c2 != null && (c3 != null || c4 != null)) {

        c1.moveToFirst();
        c2.moveToFirst();
        c3.moveToFirst();
        c4.moveToFirst();

        autoLoginCredentials.setAccessToken(c1.getString(c1.getColumnIndex("userToken")));
        autoLoginCredentials.setRefreshToken(c2.getString(c2.getColumnIndex("userRefreshToken")));
        autoLoginCredentials.setStoreName(c3.getString(c3.getColumnIndex("userRepo")));
        autoLoginCredentials.setEmail(c4.getString(c4.getColumnIndex("loginName")));

        c1.close();
        c2.close();
        c3.close();
        c4.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Single.error(e);
    }
    return Single.just(autoLoginCredentials);
  }

  public boolean isNullOrEmpty(String str) {
    if (str != null && !str.trim()
        .isEmpty()) {
      return false;
    }
    return true;
  }
}
