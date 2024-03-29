package com.example.jacob.bluetoothtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

public class MatchActivity extends AppCompatActivity {

    private ActionBar m_actionBar;
    private ScoutingForm m_currentForm;
    private Button m_autoButton, m_teleopButton, m_endgameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get the current scouting form from the main activity
        m_currentForm = (ScoutingForm) getIntent().getSerializableExtra("SCOUTING_FORM");

        // Action Bar stuff
        m_actionBar = getSupportActionBar();
        int teamColor = (m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam));
        m_actionBar.setBackgroundDrawable(new ColorDrawable(teamColor));

        // Get all the buttons
        m_autoButton = findViewById(R.id.auto);
        m_teleopButton = findViewById(R.id.teleop);
        m_endgameButton = findViewById(R.id.endgame);

        handlePhaseChange(m_currentForm.currentMode);

        m_autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhaseChange(Constants.GameMode.AUTO);
            }
        });

        m_teleopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhaseChange(Constants.GameMode.TELEOP);
            }
        });

        m_endgameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhaseChange(Constants.GameMode.ENDGAME);
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting Match")
                .setMessage("Are you sure you want to finish this match?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_currentForm.complete();
                        Intent intent = new Intent(MatchActivity.this, MainActivity.class);
                        intent.putExtra("SCOUTING_FORM", m_currentForm);
                        startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void handlePhaseChange(Constants.GameMode gameMode) {
        m_currentForm.currentMode = gameMode;

        // Create the default gray button
        Drawable defaultButtonBackground = getResources().getDrawable(R.drawable.rounded_button);
        ColorFilter defaultButtonTint = new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        defaultButtonBackground.setColorFilter(defaultButtonTint);

        // Highlight the selected button while setting the others to default
        if (gameMode == Constants.GameMode.AUTO) {
            Drawable autoButtonBackground = getResources().getDrawable(R.drawable.rounded_button);
            ColorFilter autoButtonTint = new PorterDuffColorFilter(getResources().getColor(R.color.orangeColor), PorterDuff.Mode.MULTIPLY);
            autoButtonBackground.setColorFilter(autoButtonTint);

            m_autoButton.setBackground(autoButtonBackground);
            m_teleopButton.setBackground(defaultButtonBackground);
            m_endgameButton.setBackground(defaultButtonBackground);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, AutoFragment.newInstance(m_currentForm))
                    .commit();
        } else if (gameMode == Constants.GameMode.TELEOP) {
            Drawable teleopButtonBackground = getResources().getDrawable(R.drawable.rounded_button);
            ColorFilter teleopButtonTint = new PorterDuffColorFilter(getResources().getColor(R.color.purpleColor), PorterDuff.Mode.MULTIPLY);
            teleopButtonBackground.setColorFilter(teleopButtonTint);

            m_autoButton.setBackground(defaultButtonBackground);
            m_teleopButton.setBackground(teleopButtonBackground);
            m_endgameButton.setBackground(defaultButtonBackground);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, TeleopFragment.newInstance(m_currentForm))
                    .commit();
        } else {
            Drawable endgameButtonBackground = getResources().getDrawable(R.drawable.rounded_button);
            ColorFilter endgameButtonTint = new PorterDuffColorFilter(getResources().getColor(R.color.greenColor), PorterDuff.Mode.MULTIPLY);
            endgameButtonBackground.setColorFilter(endgameButtonTint);

            m_autoButton.setBackground(defaultButtonBackground);
            m_teleopButton.setBackground(defaultButtonBackground);
            m_endgameButton.setBackground(endgameButtonBackground);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, EndgameFragment.newInstance(m_currentForm))
                    .commit();
        }

        updateActionBar();
    }

    private void updateActionBar() {
        m_actionBar.setTitle("Team " + m_currentForm.teamNumber + " - " + m_currentForm.currentMode.toString() + "  (Match #" + m_currentForm.matchNumber + ")");
    }
}