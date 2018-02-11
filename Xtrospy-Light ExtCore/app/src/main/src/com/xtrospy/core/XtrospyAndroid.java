package com.xtrospy.core;

import android.app.Application;
import android.content.Context;

public class XtrospyAndroid extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        XtrospyAndroid.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return XtrospyAndroid.context;
    }
}
