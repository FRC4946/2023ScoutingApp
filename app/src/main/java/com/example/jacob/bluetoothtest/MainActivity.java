package com.example.jacob.bluetoothtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private ToggleButton m_autoCross;

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

        m_autoCross = findViewById(R.id.autoLine);

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
                form.teamNumber = m_currentForm.teamNumber;
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
        m_teamInfo.setText((m_currentForm.scoutName.length() > Constants.SCOUT_NAME_MAX_UI_LENGTH ? m_currentForm.scoutName.substring(0, Constants.SCOUT_NAME_MAX_UI_LENGTH - 3) + "..." : m_currentForm.scoutName) + " Scouting " + m_currentForm.team + " - " + m_currentForm.teamNumber + " For Match " + m_currentForm.matchNumber);
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
                m_autoCross.setEnabled(true);
                m_autoCross.setVisibility(View.VISIBLE);
                m_defenceModeLayout.setVisibility(View.GONE);
                m_defenceTypeLayout.setVisibility(View.GONE);
            } else {

                m_auto.setEnabled(true);
                m_autoCross.setEnabled(false);
                m_autoCross.setVisibility(View.GONE);

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

        View layout = inflater.inflate(R.layout.dialog_target, null);

        //setup

        ((TextView) findViewById(R.id.targetTitle)).setText(m_currentForm.currentMode == Constants.GameMode.AUTO ? "Auto Shots" : "Trench Shots");

        final EditText scored = layout.findViewById(R.id.scored);
        final EditText missed = layout.findViewById(R.id.missed);

        scored.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoBalls : m_currentForm.trenchBalls));
        missed.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoBallsShot - m_currentForm.autoBalls : m_currentForm.trenchBallsShot - m_currentForm.trenchBalls));

        final Button addScoredHigh = layout.findViewById(R.id.addScored);
        final Button addMissedHigh = layout.findViewById(R.id.addMissed);

        addScoredHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scored.setText("" + (Integer.parseInt(scored.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addMissedHigh.setOnClickListener(new View.OnClickListener() {
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
                EditText scored = ((AlertDialog) dialogInterface).findViewById(R.id.scored);
                EditText missed = ((AlertDialog) dialogInterface).findViewById(R.id.missed);

                try {
                    if (m_currentForm.currentMode == Constants.GameMode.AUTO) {
                        m_currentForm.autoBalls = Integer.parseInt(scored.getText().toString());
                        m_currentForm.autoBallsShot = Integer.parseInt(scored.getText().toString()) + Integer.parseInt(missed.getText().toString());
                    } else {
                        m_currentForm.trenchBalls = Integer.parseInt(scored.getText().toString());
                        m_currentForm.trenchBallsShot = Integer.parseInt(scored.getText().toString()) + Integer.parseInt(missed.getText().toString());
                    }
                } catch (NumberFormatException e) {

                }
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

        final EditText name = layout.findViewById(R.id.scoutName);
        final EditText number = layout.findViewById(R.id.matchNumber);
        final EditText teamNumber = layout.findViewById(R.id.teamNumber);

        name.setText(m_currentForm.scoutName);
        number.setText("" + m_currentForm.matchNumber);
        teamNumber.setText("" + m_currentForm.teamNumber);

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
                updateShelf();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showStationAlert() {

    }

    void showTargetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_target, null);

        //setup

        ((TextView) findViewById(R.id.targetTitle)).setText(m_currentForm.currentMode == Constants.GameMode.AUTO ? "Auto Shots" : "Close To Target Shots");

        final EditText scored = layout.findViewById(R.id.scored);
        final EditText missed = layout.findViewById(R.id.missed);

        scored.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoBalls : m_currentForm.targetBalls));
        missed.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoBallsShot - m_currentForm.autoBalls : m_currentForm.targetBallsShot - m_currentForm.targetBalls));

        final Button addScoredHigh = layout.findViewById(R.id.addScored);
        final Button addMissedHigh = layout.findViewById(R.id.addMissed);

        addScoredHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scored.setText("" + (Integer.parseInt(scored.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addMissedHigh.setOnClickListener(new View.OnClickListener() {
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
                EditText scored = ((AlertDialog) dialogInterface).findViewById(R.id.scored);
                EditText missed = ((AlertDialog) dialogInterface).findViewById(R.id.missed);

                try {
                    if (m_currentForm.currentMode == Constants.GameMode.AUTO) {
                        m_currentForm.autoBalls = Integer.parseInt(scored.getText().toString());
                        m_currentForm.autoBallsShot = Integer.parseInt(scored.getText().toString()) + Integer.parseInt(missed.getText().toString());
                    } else {
                        m_currentForm.targetBalls = Integer.parseInt(scored.getText().toString());
                        m_currentForm.targetBallsShot = Integer.parseInt(scored.getText().toString()) + Integer.parseInt(missed.getText().toString());
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showMidFieldAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.dialog_target, null);

        //setup

        ((TextView) findViewById(R.id.targetTitle)).setText(m_currentForm.currentMode == Constants.GameMode.AUTO ? "Auto Shots" : "Mid Field Shots");

        final EditText scored = layout.findViewById(R.id.scored);
        final EditText missed = layout.findViewById(R.id.missed);

        scored.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoBalls : m_currentForm.fieldBalls));
        missed.setText("" + (m_currentForm.currentMode == Constants.GameMode.AUTO ? m_currentForm.autoBallsShot - m_currentForm.autoBalls : m_currentForm.fieldBallsShot - m_currentForm.fieldBalls));

        final Button addScoredHigh = layout.findViewById(R.id.addScored);
        final Button addMissedHigh = layout.findViewById(R.id.addMissed);

        addScoredHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    scored.setText("" + (Integer.parseInt(scored.getText().toString()) + 1));
                } catch (NumberFormatException e) {

                }
            }
        });

        addMissedHigh.setOnClickListener(new View.OnClickListener() {
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
                EditText scored = ((AlertDialog) dialogInterface).findViewById(R.id.scored);
                EditText missed = ((AlertDialog) dialogInterface).findViewById(R.id.missed);

                try {
                    if (m_currentForm.currentMode == Constants.GameMode.AUTO) {
                        m_currentForm.autoBalls = Integer.parseInt(scored.getText().toString());
                        m_currentForm.autoBallsShot = Integer.parseInt(scored.getText().toString()) + Integer.parseInt(missed.getText().toString());
                    } else {
                        m_currentForm.fieldBalls = Integer.parseInt(scored.getText().toString());
                        m_currentForm.fieldBallsShot = Integer.parseInt(scored.getText().toString()) + Integer.parseInt(missed.getText().toString());
                    }
                } catch (NumberFormatException e) {

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void showClimbAlert() {

    }
}


