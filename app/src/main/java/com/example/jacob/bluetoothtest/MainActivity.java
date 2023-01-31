package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;
import com.example.jacob.bluetoothtest.forms.TimePeriod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //https://coolors.co/e73c37-eef4d4-ffd6e0-657ed4-262626
    private ScoutingForm m_currentForm = new ScoutingForm();
    private Button m_new, m_load, m_save, m_send, m_matchToggle;
    private String m_loadName;
    private TextView m_teamInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        m_loadName = intent.getStringExtra(intent.EXTRA_TEXT); //Gets message from previous activity, stores in message string

        m_teamInfo = findViewById(R.id.teamInfo);

        m_matchToggle = findViewById(R.id.toggleMatch);

        m_new = findViewById(R.id.newForm);
        m_save = findViewById(R.id.save);
        m_load = findViewById(R.id.load);
        m_send = findViewById(R.id.send);

        updateTeamInfo();

        m_matchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.matchStarted = true;
                m_currentForm.currentMode = Constants.GameMode.AUTO;
                Intent intent = new Intent(MainActivity.this, LoadActivity.class);
                startActivity(intent);
            }
        });

        m_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScoutingForm form = new ScoutingForm();
                form.team = m_currentForm.team;
                form.teamNumber = m_currentForm.teamNumber;
                form.scoutName = m_currentForm.scoutName;
                form.matchNumber = m_currentForm.matchNumber + 1;
                m_currentForm = form;
                showSetupAlert();
            }
        });

        m_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constants.WRITE_LOG_REQUEST);
            }
        });

        m_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoadActivity.class);
                startActivity(intent);
            }
        });

        m_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
                String message = m_currentForm.toString();
                intent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(intent);
            }
        });

        if (m_loadName != null) {
            Log.i("A", "File recieved, loading" + m_loadName);

            try {
                BufferedReader reader = new BufferedReader(new FileReader(new File(m_loadName)));
                String content = reader.readLine(); //Gets the content of the file
                m_currentForm = ScoutingForm.fromString(content);
            } catch (FileNotFoundException e) {
                Log.i("A", "File Not Found");
            } catch (IOException e) {
                Log.i("A", "Error Reading File");
            }
            updateTeamInfo();
        } else {
            showSetupAlert();
        }
    }

    void updateTeamInfo() {
        m_teamInfo.setText((m_currentForm.scoutName.length() > Constants.SCOUT_NAME_MAX_UI_LENGTH ? m_currentForm.scoutName.substring(0, Constants.SCOUT_NAME_MAX_UI_LENGTH - 3) + "..." : m_currentForm.scoutName) + " Scouting " + m_currentForm.team + " - " + m_currentForm.teamNumber + " For Match " + m_currentForm.matchNumber);
        m_teamInfo.setTextColor((m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam)));
        m_load.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
        m_new.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
        m_send.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
        m_save.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
    }

    void showSetupAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_setup, null);

        //setup

        final Button red = layout.findViewById(R.id.red);
        final Button blue = layout.findViewById(R.id.blue);

        final EditText name = layout.findViewById(R.id.scoutName);
        final EditText number = layout.findViewById(R.id.matchNumber);
        final EditText teamNumber = layout.findViewById(R.id.teamNumber);

        name.setText(m_currentForm.scoutName);
        number.setText("" + m_currentForm.matchNumber);
        teamNumber.setText("" + m_currentForm.teamNumber);

        if (m_currentForm.team == Constants.Team.RED) {
            red.setEnabled(false);
            red.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonSelectedColor)));
            blue.setEnabled(true);
            blue.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonNotSelectedColor)));
        } else {
            red.setEnabled(true);
            red.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonNotSelectedColor)));
            blue.setEnabled(false);
            blue.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonSelectedColor)));
        }

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.team = Constants.Team.RED;
                red.setEnabled(false);
                red.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonSelectedColor)));
                blue.setEnabled(true);
                blue.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonNotSelectedColor)));
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.team = Constants.Team.BLUE;
                red.setEnabled(true);
                red.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonNotSelectedColor)));
                blue.setEnabled(false);
                blue.setBackground(new ColorDrawable(getResources().getColor(R.color.buttonSelectedColor)));
            }
        });

        builder.setView(layout);

        builder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                m_currentForm.scoutName = name.getText().toString();
                m_currentForm.matchNumber = Integer.parseInt(number.getText().toString());
                m_currentForm.teamNumber = Integer.parseInt(teamNumber.getText().toString());
                updateTeamInfo();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == Constants.WRITE_LOG_REQUEST) {

            Log.i("A", "Received response for write permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When write permission is granted

                Log.i("A", "Write permission has now been granted.");

                File home = new File(getApplicationInfo().dataDir + "/Logs"); //application directory, so: /data/data/com.whatever.otherstuff/Logs
                home.mkdirs();
                File csv = new File(home.getAbsolutePath() + "/" + m_currentForm.matchNumber + "-" + m_currentForm.teamNumber);

                try {


                    BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
                    writer.write(m_currentForm.toString());
                    writer.close();

                    Log.i("A", "Finished writing to " + csv.getAbsolutePath() + "");

                    //IO crap for testing
                    //BufferedReader reader = new BufferedReader(new FileReader(csv));
                    //reader.close();
                } catch (IOException e) {
                    //IO didn't work, honestly this shouldn't happen, if it does its probably a device or permission error
                    Log.i("A", "File write to " + csv.getAbsolutePath() + " failed");
                }

            } else {
                Log.i("A", "Write permission was NOT granted.");
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
}


