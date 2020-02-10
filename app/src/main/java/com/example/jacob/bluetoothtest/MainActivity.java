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

    private Button m_redTrench, m_blueTrench, m_redTarget, m_blueTarget, m_shieldGenerator, m_auto, m_teleop, m_endgame, m_offence, m_defence, m_defending, m_idle, m_new, m_load, m_save, m_send, m_matchToggle;
    private ImageButton m_shelfButton;
    private LinearLayout m_shelfLayout, m_defenceModeLayout, m_defenceTypeLayout;

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

        m_defenceModeLayout = findViewById(R.id.defenceMode);
        m_defenceTypeLayout = findViewById(R.id.defenceType);

        m_teamInfo = findViewById(R.id.teamInfo);

        m_matchToggle = findViewById(R.id.toggleMatch);

        m_new = findViewById(R.id.newForm);
        m_save = findViewById(R.id.save);
        m_load = findViewById(R.id.load);
        m_send = findViewById(R.id.send);

        updateTeamInfo();
        updateShelf();

        m_matchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_currentForm.matchStarted) {
                    m_currentForm.matchOver = true;
                    m_matchToggle.setEnabled(false);
                } else {
                    m_currentForm.matchStarted = true;
                    m_currentForm.currentMode = Constants.GameMode.AUTO;
                }
                updateShelf();
            }
        });

        m_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScoutingForm form = new ScoutingForm();
                form.team = m_currentForm.team;
                form.driverStation = m_currentForm.driverStation;
                form.scoutName = m_currentForm.scoutName;
                form.matchNumber = m_currentForm.matchNumber + 1;
                m_currentForm = form;
                showSetupAlert();
            }
        });

        m_redTrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    if (m_currentForm.team == Constants.Team.RED) {
                        showTrenchAlert();
                    }
                }
            }
        });

        m_blueTrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShelfOut()) {
                    if (m_currentForm.team == Constants.Team.BLUE) {
                        showTrenchAlert();
                    }
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
                        if (m_currentForm.currentMode != Constants.GameMode.AUTO) {
                            showStationAlert();
                        }
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
                        if (m_currentForm.currentMode != Constants.GameMode.AUTO) {
                            showStationAlert();
                        }
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
                    if (m_currentForm.matchStarted && !m_currentForm.matchOver) {
                        m_shelfLayout.setVisibility(View.GONE);
                    } else if (m_currentForm.matchStarted && m_currentForm.matchOver) {
                        Utilities.showToast(getBaseContext(), Constants.MATCH_OVER_ERROR, Toast.LENGTH_SHORT);
                    } else {
                        Utilities.showToast(getBaseContext(), Constants.START_MATCH_ERROR, Toast.LENGTH_SHORT);
                    }
                } else {
                    m_shelfLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        m_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.currentMode = Constants.GameMode.AUTO;
                updateShelf();
            }
        });

        m_teleop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.currentMode = Constants.GameMode.TELEOP;
                updateShelf();
            }
        });

        m_endgame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.currentMode = Constants.GameMode.ENDGAME;
                updateShelf();
            }
        });

        showSetupAlert();
    }

    boolean isShelfOut() {
        return m_shelfLayout.getVisibility() != View.GONE;
    }

    void updateTeamInfo() {
        m_teamInfo.setText((m_currentForm.scoutName.length() > Constants.SCOUT_NAME_MAX_UI_LENGTH ? m_currentForm.scoutName.substring(0, Constants.SCOUT_NAME_MAX_UI_LENGTH - 3) + "..." : m_currentForm.scoutName) + " Scouting " + m_currentForm.team + " - " + m_currentForm.driverStation + " For Match " + m_currentForm.matchNumber);
        m_teamInfo.setTextColor((m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam)));
    }

    void updateShelf() {
        if (m_currentForm.matchStarted && !m_currentForm.matchOver) {
            m_new.setEnabled(false);
            m_send.setEnabled(false);
            m_save.setEnabled(false);
            m_load.setEnabled(false);

            if (m_currentForm.currentMode == Constants.GameMode.AUTO) {
                m_auto.setEnabled(false);
                m_teleop.setEnabled(true);
                m_endgame.setEnabled(true);
                m_defenceModeLayout.setVisibility(View.GONE);
                m_defenceTypeLayout.setVisibility(View.GONE);
            } else {

                m_auto.setEnabled(true);

                if (m_currentForm.currentMode == Constants.GameMode.TELEOP) {
                    m_teleop.setEnabled(false);
                    m_endgame.setEnabled(true);
                } else {
                    m_teleop.setEnabled(true);
                    m_endgame.setEnabled(false);
                }

                m_defenceModeLayout.setVisibility(View.VISIBLE);

                if (m_currentForm.currentAction == Constants.GameAction.DEFENCE) {

                    m_defence.setEnabled(false);
                    m_offence.setEnabled(true);
                    m_defenceTypeLayout.setVisibility(View.VISIBLE);

                    if (m_currentForm.currentDefenceType == Constants.DefenceType.DEFENDING) {
                        m_defending.setEnabled(false);
                        m_idle.setEnabled(true);
                    } else {
                        m_defending.setEnabled(true);
                        m_idle.setEnabled(false);
                    }
                } else {
                    m_defence.setEnabled(false);
                    m_offence.setEnabled(true);
                    m_defenceTypeLayout.setVisibility(View.GONE);
                }
            }
        } else {
            m_new.setEnabled(true);
            m_send.setEnabled(true);
            m_save.setEnabled(true);
            m_load.setEnabled(true);

            m_auto.setEnabled(false);
            m_teleop.setEnabled(false);
            m_endgame.setEnabled(false);
            m_defenceModeLayout.setVisibility(View.GONE);
            m_defenceTypeLayout.setVisibility(View.GONE);
        }
        if (m_currentForm.matchOver) {
            m_matchToggle.setEnabled(false);
            m_matchToggle.setText("Match Over");
        } else {
            m_matchToggle.setEnabled(true);

            if (m_currentForm.matchStarted) {
                m_matchToggle.setText("End Match");
            } else {
                m_matchToggle.setText("Start Match");
            }
        }
    }

    void showTrenchAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_trench, null);

        //setup

        final ToggleButton stage2Attempted = layout.findViewById(R.id.attemptedStage2);
        final ToggleButton stage2Succeeded = layout.findViewById(R.id.succeededStage2);

        final ToggleButton stage3Attempted = layout.findViewById(R.id.attemptedStage3);
        final ToggleButton stage3Succeeded = layout.findViewById(R.id.succeededStage3);

        stage2Attempted.setChecked(m_currentForm.attemptedStage2);
        stage2Succeeded.setChecked(m_currentForm.successStage2);

        stage2Succeeded.setEnabled(m_currentForm.attemptedStage2);

        stage3Attempted.setChecked(m_currentForm.attemptedStage3);
        stage3Succeeded.setChecked(m_currentForm.successStage3);

        stage3Succeeded.setEnabled(m_currentForm.attemptedStage3);

        stage2Attempted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage2Attempted.isChecked()) {
                    stage2Succeeded.setEnabled(true);
                } else {
                    stage2Succeeded.setEnabled(false);
                    stage2Succeeded.setChecked(false);
                }
            }
        });

        stage3Attempted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage3Attempted.isChecked()) {
                    stage3Succeeded.setEnabled(true);
                } else {
                    stage3Succeeded.setEnabled(false);
                    stage3Succeeded.setChecked(false);
                }
            }
        });

        builder.setView(layout);

        builder.setPositiveButton(R.string.affirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ToggleButton stage2Attempted = ((AlertDialog) dialogInterface).findViewById(R.id.attemptedStage2);
                ToggleButton stage2Succeeded = ((AlertDialog) dialogInterface).findViewById(R.id.succeededStage2);

                ToggleButton stage3Attempted = ((AlertDialog) dialogInterface).findViewById(R.id.attemptedStage3);
                ToggleButton stage3Succeeded = ((AlertDialog) dialogInterface).findViewById(R.id.succeededStage3);

                m_currentForm.attemptedStage2 = stage2Attempted.isChecked();
                m_currentForm.successStage2 = stage2Succeeded.isChecked();

                m_currentForm.attemptedStage3 = stage3Attempted.isChecked();
                m_currentForm.successStage3 = stage3Succeeded.isChecked();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showSetupAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_setup, null);

        //setup

        final Button red = layout.findViewById(R.id.red);
        final Button blue = layout.findViewById(R.id.blue);

        final Button one = layout.findViewById(R.id.one);
        final Button two = layout.findViewById(R.id.two);
        final Button three = layout.findViewById(R.id.three);

        final EditText name = layout.findViewById(R.id.scoutName);
        final EditText number = layout.findViewById(R.id.matchNumber);

        name.setText(m_currentForm.scoutName);
        number.setText("" + m_currentForm.matchNumber);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.team = Constants.Team.RED;
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.team = Constants.Team.BLUE;
            }
        });

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.driverStation = 1;
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.driverStation = 2;
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_currentForm.driverStation = 3;
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
                updateTeamInfo();
                updateShelf();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showStationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_intake, null);

        //setup

        final EditText loaded = layout.findViewById(R.id.loaded);
        final EditText missed = layout.findViewById(R.id.missed);

        loaded.setText("" + m_currentForm.loadingStationIntake);
        missed.setText("" + (m_currentForm.loadingStationIntakeAttempt - m_currentForm.loadingStationIntake));

        final Button addLoaded = layout.findViewById(R.id.addLoaded);
        final Button addMissed = layout.findViewById(R.id.addMissed);

        addLoaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loaded.setText("" + (Integer.parseInt(loaded.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addMissed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    missed.setText("" + (Integer.parseInt(missed.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        builder.setView(layout);

        builder.setPositiveButton(R.string.affirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText loaded = ((AlertDialog) dialogInterface).findViewById(R.id.loaded);
                EditText missed = ((AlertDialog) dialogInterface).findViewById(R.id.missed);

                try {
                    m_currentForm.loadingStationIntake = Integer.parseInt(loaded.getText().toString());
                    m_currentForm.loadingStationIntakeAttempt = Integer.parseInt(loaded.getText().toString()) + Integer.parseInt(missed.getText().toString());
                } catch (NumberFormatException e) {

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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


