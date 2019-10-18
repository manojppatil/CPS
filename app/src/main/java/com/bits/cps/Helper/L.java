package com.bits.cps.Helper;

import android.util.Log;

public class L {
    public static void L(String message) {
        Log.d("Debug functioin", message);
    }

    public static void L(String key, String message) {
        Log.d(key, message);
    }
}
