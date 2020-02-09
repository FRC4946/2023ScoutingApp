package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Log;import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.util.ArrayList;

import android.provider.Settings.Secure;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

/**WARNING TO WHOEVER IS READING THIS CODE, THIS WAS A LEARNING EXPERIENCE FOR ME
 * IF I WAS GOING TO DO IT AGAIN I WOULD DO IT VERY DIFFERENTLY AND MUCH BETTER
 * I DON'T HAVE TIME TO REDO EVERYTHING SO MOST OF THIS WILL PROBABLY NEVER BE FIXED
 * MOST OF THIS IS REALLY DUMB SPAGHETTI CODE AND I'M SORRY YOU NEED TO READ THROUGH IT
 */
public class MainActivity extends AppCompatActivity {

    Button m_redTrench, m_blueTrench, m_redTarget, m_blueTarget, m_shieldGenerator;

    /** Launches when activity starts
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_redTarget = findViewById(R.id.redTarget);
        m_blueTarget = findViewById(R.id.blueTarget);
        m_redTrench = findViewById(R.id.redTrench);
        m_blueTrench = findViewById(R.id.blueTrench);
        m_shieldGenerator = findViewById(R.id.shieldGenerator);

        m_redTrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("A", "Clicked Red Trench");
            }
        });

        m_blueTrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("A", "Clicked Blue Trench");
            }
        });

        m_redTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("A", "Clicked Red Target");
            }
        });

        m_blueTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("A", "Clicked Blue Target");
            }
        });

        m_shieldGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("A", "Clicked Shield Generator");
            }
        });
    }

}


