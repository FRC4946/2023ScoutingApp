package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String m_station;

    private EditText m_teamNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        m_loadName = intent.getStringExtra(intent.EXTRA_TEXT); //Gets message from previous activity, stores in message string
        if (intent.hasExtra("SCOUTING_FORM")) {
            m_currentForm = (ScoutingForm) intent.getSerializableExtra("SCOUTING_FORM");
        }

        m_teamInfo = findViewById(R.id.teamInfo);

        m_matchToggle = findViewById(R.id.toggleMatch);

        m_new = findViewById(R.id.newForm);
        m_save = findViewById(R.id.save);
        m_load = findViewById(R.id.load);
        m_send = findViewById(R.id.send);

        updateUI();

        m_matchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.matchStarted = true;
                m_currentForm.currentMode = Constants.GameMode.AUTO;
                Intent intent = new Intent(MainActivity.this, MatchActivity.class);
                intent.putExtra("SCOUTING_FORM", m_currentForm);
                startActivity(intent);
            }
        });

        m_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                m_currentForm.setFinalized(true);
            } catch (FileNotFoundException e) {
                Log.i("A", "File Not Found");
            } catch (IOException e) {
                Log.i("A", "Error Reading File");
            }
            updateUI();
        }
    }

    void updateUI() {
        m_teamInfo.setText((m_currentForm.scoutName.length() > Constants.SCOUT_NAME_MAX_UI_LENGTH ? m_currentForm.scoutName.substring(0, Constants.SCOUT_NAME_MAX_UI_LENGTH - 3) + "..." : m_currentForm.scoutName) + " Scouting " + m_currentForm.team + " - " + m_currentForm.teamNumber + " For Match " + m_currentForm.matchNumber);
        m_teamInfo.setTextColor((m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam)));
        m_load.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
        m_new.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
        m_send.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
        m_save.setBackground((m_currentForm.team == Constants.Team.RED) ? getResources().getDrawable(R.drawable.redbutton) : getResources().getDrawable(R.drawable.bluebutton));
    }

    void showSetupAlert() {
        ScoutingForm form = new ScoutingForm();
        form.team = m_currentForm.team;
        form.teamNumber = m_currentForm.teamNumber;
        form.scoutName = m_currentForm.scoutName;
        form.matchNumber = m_currentForm.matchNumber + 1;
        m_currentForm = form;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_setup, null);

        //setup

        final Button red = layout.findViewById(R.id.red);
        final Button blue = layout.findViewById(R.id.blue);

        final EditText name = layout.findViewById(R.id.scoutName);
        final EditText number = layout.findViewById(R.id.matchNumber);
        m_teamNumber = layout.findViewById(R.id.teamNumber);

        final Spinner teamPicker = layout.findViewById(R.id.teamPicker);

        String[] teams = {"Red 1", "Red 2", "Red 3", "Blue 1", "Blue 2", "Blue 3"};

        ArrayAdapter aa = new ArrayAdapter(this, R.layout.spinner_template, teams);

        aa.setDropDownViewResource(R.layout.spinner_dropdown_template);
        teamPicker.setAdapter(aa);

        name.setText(m_currentForm.scoutName);
        number.setText("" + m_currentForm.matchNumber);
        m_teamNumber.setText("" + m_currentForm.teamNumber);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        m_station = sharedPref.getString("station", "Red 1");
        switch (m_station) {
            case "Red 1":
                teamPicker.setSelection(0);
                break;
            case "Red 2":
                teamPicker.setSelection(1);
                break;
            case "Red 3":
                teamPicker.setSelection(2);
                break;
            case "Blue 1":
                teamPicker.setSelection(3);
                break;
            case "Blue 2":
                teamPicker.setSelection(4);
                break;
            case "Blue 3":
                teamPicker.setSelection(5);
                break;
        }

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

                // Try and get the opponent robots from JSON file
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.READ_SCHEDULE_REQUEST);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                m_currentForm.scoutName = name.getText().toString();
                m_currentForm.matchNumber = Integer.parseInt(number.getText().toString());
                m_currentForm.teamNumber = Integer.parseInt(m_teamNumber.getText().toString());
                updateUI();
            }
        });

        teamPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.i("Debug", "On Item Selected");
                m_station = teamPicker.getSelectedItem().toString();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("station", m_station);
                editor.apply();

                // Try to get the team number from the JSON file
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.READ_TEAM_NUMBER_REQUEST);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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
                    Utilities.showToast(this, "SAVED!", 2);

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

        } else if (requestCode == Constants.READ_SCHEDULE_REQUEST) {

            Log.i("A", "Received response for read permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When read permission is granted

                Log.i("A", "Read permission has now been granted.");

                try {

                    // Read the JSON File in the Documents Folder
                    // *Technically a txt file
                    BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/Documents/schedule.txt"));
                    JSONArray schedule = new JSONObject(reader.readLine()).getJSONArray("Schedule");

                    for (int i=0; i < schedule.length(); i++)
                    {
                        try {
                            JSONObject match = schedule.getJSONObject(i);

                            // Pull match number
                            int matchNumber = match.getInt("matchNumber");
                            if (matchNumber == m_currentForm.matchNumber) {
                                JSONArray teams = match.getJSONArray("teams");

                                if (m_currentForm.team == Constants.Team.RED) {
                                    m_currentForm.opponentA = teams.getJSONObject(3).getInt("teamNumber");
                                    m_currentForm.opponentB = teams.getJSONObject(4).getInt("teamNumber");
                                    m_currentForm.opponentC = teams.getJSONObject(5).getInt("teamNumber");
                                } else {
                                    m_currentForm.opponentA = teams.getJSONObject(0).getInt("teamNumber");
                                    m_currentForm.opponentB = teams.getJSONObject(1).getInt("teamNumber");
                                    m_currentForm.opponentC = teams.getJSONObject(2).getInt("teamNumber");
                                }

                                break;
                            }
                        } catch (JSONException e) {
                            // Oops
                            Log.i("A", e.toString());
                        }
                    }


                    reader.close();

                } catch (IOException e) {
                    //IO didn't work, honestly this shouldn't happen, if it does its probably a device or permission error
                    Log.i("A", "File read failed");
                } catch (JSONException e) {
                    Log.i("A", "JSON Parse failed");
                }

            } else {
                Log.i("A", "Read permission was NOT granted.");
            }

        } else if (requestCode == Constants.READ_TEAM_NUMBER_REQUEST) {

            Log.i("A", "Received response for read permission request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When read permission is granted

                Log.i("A", "Read permission has now been granted.");

                try {

                    // Read the JSON File in the Documents Folder
                    // *Technically a txt file
                    BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory().getPath() + "/Documents/schedule.txt"));
                    JSONArray schedule = new JSONObject(reader.readLine()).getJSONArray("Schedule");

                    for (int i=0; i < schedule.length(); i++)
                    {
                        try {
                            JSONObject match = schedule.getJSONObject(i);

                            // Pull match number
                            int matchNumber = match.getInt("matchNumber");
                            if (matchNumber == m_currentForm.matchNumber) {
                                JSONArray teams = match.getJSONArray("teams");

                                switch(m_station) {
                                    case "Red 1":
                                        m_currentForm.teamNumber = teams.getJSONObject(0).getInt("teamNumber");
                                        break;
                                    case "Red 2":
                                        m_currentForm.teamNumber = teams.getJSONObject(1).getInt("teamNumber");
                                        break;
                                    case "Red 3":
                                        m_currentForm.teamNumber = teams.getJSONObject(2).getInt("teamNumber");
                                        break;
                                    case "Blue 1":
                                        m_currentForm.teamNumber = teams.getJSONObject(3).getInt("teamNumber");
                                        break;
                                    case "Blue 2":
                                        m_currentForm.teamNumber = teams.getJSONObject(4).getInt("teamNumber");
                                        break;
                                    case "Blue 3":
                                        m_currentForm.teamNumber = teams.getJSONObject(5).getInt("teamNumber");
                                        break;
                                }

                                m_teamNumber.setText("" + m_currentForm.teamNumber);
                            }
                        } catch (JSONException e) {
                            // Oops
                            Log.i("A", e.toString());
                        }
                    }


                    reader.close();

                } catch (IOException e) {
                    //IO didn't work, honestly this shouldn't happen, if it does its probably a device or permission error
                    Log.i("A", "File read failed");
                } catch (JSONException e) {
                    Log.i("A", "JSON Parse failed");
                }

            } else {
                Log.i("A", "Read permission was NOT granted.");
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}


