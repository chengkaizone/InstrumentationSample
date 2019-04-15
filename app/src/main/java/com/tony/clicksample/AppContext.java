package com.tony.clicksample;

import android.app.Application;
import android.content.Intent;

/**
 * Created by lance on 16/5/6.
 */
public class AppContext extends Application {
    public static final String TAG = "AppContext";

    public static AppContext mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        startService(new Intent(this, DomainService.class));

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static String getAppPackageName() {

        return mApplication.getPackageName();
    }

    public static AppContext shared() {

        return mApplication;
    }

}
