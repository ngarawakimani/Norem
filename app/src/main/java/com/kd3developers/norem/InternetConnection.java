package com.kd3developers.norem;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by wa kimani on 12/1/2016.
 */

public class InternetConnection {

    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(Context context) {
        return  ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}

