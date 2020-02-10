package com.example.jacob.bluetoothtest;

import android.content.Context;
import android.widget.Toast;

public class Utilities {

    private static Toast m_appToast;

    public static void showToast(Context context, String text, int duration) {

        if (m_appToast != null) {
            m_appToast.cancel();
        }

        m_appToast = Toast.makeText(context, text, duration);
        m_appToast.show();
    }

}
