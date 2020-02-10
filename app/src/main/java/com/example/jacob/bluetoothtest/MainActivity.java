package com.example.jacob.bluetoothtest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

import org.w3c.dom.Text;

/**WARNING TO WHOEVER IS READING THIS CODE, THIS WAS A LEARNING EXPERIENCE FOR ME
 * IF I WAS GOING TO DO IT AGAIN I WOULD DO IT VERY DIFFERENTLY AND MUCH BETTER
 * I DON'T HAVE TIME TO REDO EVERYTHING SO MOST OF THIS WILL PROBABLY NEVER BE FIXED
 * MOST OF THIS IS REALLY DUMB SPAGHETTI CODE AND I'M SORRY YOU NEED TO READ THROUGH IT
 */
public class MainActivity extends AppCompatActivity {

    private ScoutingForm m_currentForm = new ScoutingForm();

    private Button m_redTrench, m_blueTrench, m_redTarget, m_blueTarget, m_shieldGenerator, m_auto, m_teleop, m_endgame, m_offence, m_defence, m_defending, m_idle;
    private ImageButton m_shelfButton;
    private LinearLayout m_shelfLayout;

    private TextView m_teamInfo;

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

        m_shelfButton = findViewById(R.id.shelfButton);

        m_shelfLayout = findViewById(R.id.modeShelf);

        m_auto = findViewById(R.id.auto);
        m_teleop = findViewById(R.id.teleop);
        m_endgame = findViewById(R.id.endgame);

        m_offence = findViewById(R.id.offence);
        m_defence = findViewById(R.id.defence);

        m_teamInfo = findViewById(R.id.teamInfo);

        updateTeamInfo();

        m_redTrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    Log.i("A", "Clicked Red Trench");
                }
            }
        });

        m_blueTrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    Log.i("A", "Clicked Blue Trench");
                }
            }
        });

        m_redTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    if (m_currentForm.team == Constants.Team.RED) {
                        showTargetAlert();
                    } else {

                    }
                }
            }
        });

        m_blueTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    if (m_currentForm.team == Constants.Team.BLUE) {
                        showTargetAlert();
                    } else {

                    }
                }
            }
        });

        m_shieldGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    Log.i("A", "Clicked Shield Generator");
                }
            }
        });

        m_shelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShelfOut()) {
                    m_shelfLayout.setVisibility(View.GONE);
                } else {
                    m_shelfLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    boolean isShelfOut() {
        return m_shelfLayout.getVisibility() != View.GONE;
    }

    void updateTeamInfo() {
        m_teamInfo.setText("" + m_currentForm.team + " - " + m_currentForm.driverStation);
        m_teamInfo.setTextColor((m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam)));
    }

    void showTargetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_target, null);

        //setup

        final EditText scoredHigh = layout.findViewById(R.id.scoredHigh);
        final EditText missedHigh = layout.findViewById(R.id.missedHigh);
        final EditText scoredLow = layout.findViewById(R.id.scoredLow);
        final EditText missedLow = layout.findViewById(R.id.missedLow);

        scoredHigh.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoHighBalls : m_currentForm.teleopHighBalls));
        missedHigh.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoHighBallsShot - m_currentForm.autoHighBalls : m_currentForm.teleopHighBallsShot - m_currentForm.teleopHighBalls));
        scoredLow.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoLowBalls : m_currentForm.teleopLowBalls));
        missedLow.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoLowBallsShot - m_currentForm.autoLowBalls : m_currentForm.teleopLowBallsShot - m_currentForm.teleopLowBalls));

        final Button addScoredHigh = layout.findViewById(R.id.addScoredHigh);
        final Button addMissedHigh = layout.findViewById(R.id.addMissedHigh);
        final Button addScoredLow = layout.findViewById(R.id.addScoredLow);
        final Button addMissedLow = layout.findViewById(R.id.addMissedLow);

        addScoredHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scoredHigh.setText("" + (Integer.parseInt(scoredHigh.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addMissedHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    missedHigh.setText("" + (Integer.parseInt(missedHigh.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addScoredLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scoredLow.setText("" + (Integer.parseInt(scoredLow.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addMissedLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    missedLow.setText("" + (Integer.parseInt(missedLow.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        builder.setView(layout);

        builder.setPositiveButton(R.string.affirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText scoredHigh = ((AlertDialog) dialogInterface).findViewById(R.id.scoredHigh);
                EditText missedHigh = ((AlertDialog) dialogInterface).findViewById(R.id.missedHigh);
                EditText scoredLow = ((AlertDialog) dialogInterface).findViewById(R.id.scoredLow);
                EditText missedLow = ((AlertDialog) dialogInterface).findViewById(R.id.missedLow);

                try {
                    if (m_currentForm.currentMode == Constants.GameMode.AUTO) {
                        m_currentForm.autoHighBalls = Integer.parseInt(scoredHigh.getText().toString());
                        m_currentForm.autoHighBallsShot = Integer.parseInt(scoredHigh.getText().toString()) + Integer.parseInt(missedHigh.getText().toString());
                        m_currentForm.autoLowBalls = Integer.parseInt(scoredLow.getText().toString());
                        m_currentForm.autoLowBallsShot = Integer.parseInt(scoredLow.getText().toString()) + Integer.parseInt(missedLow.getText().toString());
                    } else {
                        m_currentForm.teleopHighBalls = Integer.parseInt(scoredHigh.getText().toString());
                        m_currentForm.teleopHighBallsShot = Integer.parseInt(scoredHigh.getText().toString()) + Integer.parseInt(missedHigh.getText().toString());
                        m_currentForm.teleopLowBalls = Integer.parseInt(scoredLow.getText().toString());
                        m_currentForm.teleopLowBallsShot = Integer.parseInt(scoredLow.getText().toString()) + Integer.parseInt(missedLow.getText().toString());
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}


