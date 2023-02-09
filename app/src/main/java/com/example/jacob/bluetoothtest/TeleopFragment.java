package com.example.jacob.bluetoothtest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeleopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeleopFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCOUTING_FORM = "scoutingForm";
    private ScoutingForm m_currentForm;
    private ImageButton m_topCone, m_midCone, m_botCone, m_topCube, m_midCube, m_botCube;
    private CheckBox m_deleteModeSwitch;
    private TextView m_topConeCount, m_topCubeCount, m_midConeCount, m_midCubeCount, m_botConeCount, m_botCubeCount;
    private TableRow m_titleRow;
    private Button m_offenceToggle;

    // Variables for Defence Table
    private ImageButton m_playButtonA, m_playButtonB, m_playButtonC, m_resetButtonA, m_resetButtonB, m_resetButtonC;
    private TextView m_timerA, m_timerB, m_timerC;
    private EditText m_teamNumberA, m_teamNumberB, m_teamNumberC;
    private TableLayout m_defenceTable, m_offenceTable;
    private TextView m_bottomTitle;
    private Button m_loadingTimer, m_transportTimer1, m_transportTimer2, m_communityTimer;
    private int m_cyclePhase = -1; // 0: transport, 1: loading, 2: transport, 3: community

    // Whether the defence timers are running
    // Index 0: Team A Defence Timer
    // Index 1: Team B Defence Timer
    // Index 2: Team C Defence Timer
    private boolean[] m_runningDefence = {false, false, false};

    // Whether the offence timers are running
    // Index 0: Transport Timer 1
    // Index 1: Loading Timer
    // Index 2: Transport Timer 2
    // Index 3: Community Timer
    private boolean[] m_runningOffence = {false, false, false, false};
    private boolean m_offence = false; // Whether or not the robot is attacking or defending

    public TeleopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param scoutingForm The current scouting form.
     * @return A new instance of fragment TeleopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeleopFragment newInstance(ScoutingForm scoutingForm) {
        TeleopFragment fragment = new TeleopFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCOUTING_FORM, scoutingForm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_currentForm = (ScoutingForm) getArguments().getSerializable(ARG_SCOUTING_FORM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teleop, container, false);

        m_offenceToggle = view.findViewById(R.id.offence_toggle);
        m_bottomTitle = view.findViewById(R.id.bottom_title);

        runTimer();
        loadGrid(view);
        loadDefenceTable(view);
        loadOffenceTable(view);
        updateCounts();

        // Create the first cycle if there is none
        if (m_currentForm.cycleTimes.size() == 0)
            m_currentForm.cycleTimes.add(m_currentForm.currentCycle, new double[]{0, 0, 0, 0});

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Load all the stuff pertaining to the scoring, and set up button listeners
     * @param view The layout for this fragment
     */
    private void loadGrid(View view) {
        m_topCone = view.findViewById(R.id.cone_top);
        m_midCone = view.findViewById(R.id.cone_mid);
        m_botCone = view.findViewById(R.id.cone_bot);
        m_topCube = view.findViewById(R.id.cube_top);
        m_midCube = view.findViewById(R.id.cube_mid);
        m_botCube = view.findViewById(R.id.cube_bot);
        m_deleteModeSwitch = view.findViewById(R.id.delete_mode);
        m_topConeCount = view.findViewById(R.id.topConeCount);
        m_midConeCount = view.findViewById(R.id.midConeCount);
        m_botConeCount = view.findViewById(R.id.botConeCount);
        m_topCubeCount = view.findViewById(R.id.topCubeCount);
        m_midCubeCount = view.findViewById(R.id.midCubeCount);
        m_botCubeCount = view.findViewById(R.id.botCubeCount);

        m_deleteModeSwitch.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_topCone.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                m_midCone.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                m_botCone.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                m_topCube.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                m_midCube.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));
                m_botCube.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN));

                m_topCone.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                m_midCone.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                m_botCone.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                m_topCube.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                m_midCube.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));
                m_botCube.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));

                m_topConeCount.setTextColor(Color.parseColor("#FFFFFF"));
                m_midConeCount.setTextColor(Color.parseColor("#FFFFFF"));
                m_botConeCount.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                m_topCone.clearColorFilter();
                m_midCone.clearColorFilter();
                m_botCone.clearColorFilter();
                m_topCube.clearColorFilter();
                m_midCube.clearColorFilter();
                m_botCube.clearColorFilter();

                m_topCone.clearAnimation();
                m_midCone.clearAnimation();
                m_botCone.clearAnimation();
                m_topCube.clearAnimation();
                m_midCube.clearAnimation();
                m_botCube.clearAnimation();

                m_topConeCount.setTextColor(Color.parseColor("#000000"));
                m_midConeCount.setTextColor(Color.parseColor("#000000"));
                m_botConeCount.setTextColor(Color.parseColor("#000000"));
            }
        });

        m_topCone.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.conesTop = Math.max(m_currentForm.conesTop - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.conesTop = Math.min(m_currentForm.conesTop + 1, 6);
            }
            updateCounts();
        });

        m_midCone.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.conesMid = Math.max(m_currentForm.conesMid - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.conesMid = Math.min(m_currentForm.conesMid + 1, 6);
            }
            updateCounts();
        });

        m_botCone.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.conesBot = Math.max(m_currentForm.conesBot - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.conesBot = Math.min(m_currentForm.conesBot + 1, 9);
            }
            updateCounts();
        });

        m_topCube.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.cubesTop = Math.max(m_currentForm.cubesTop - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.cubesTop = Math.min(m_currentForm.cubesTop + 1, 3);
            }
            updateCounts();
        });

        m_midCube.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.cubesMid = Math.max(m_currentForm.cubesMid - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.cubesMid = Math.min(m_currentForm.cubesMid + 1, 3);
            }
            updateCounts();
        });

        m_botCube.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.cubesBot = Math.max(m_currentForm.cubesBot - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.cubesBot = Math.min(m_currentForm.cubesBot + 1, 9);
            }
            updateCounts();
        });

        m_offenceToggle.setOnClickListener(v -> {
            m_offence = !m_offence;

            if (m_offence) {
                m_offenceTable.setVisibility(View.VISIBLE);
                m_defenceTable.setVisibility(View.GONE);
                m_bottomTitle.setText("Attacking");
                m_offenceToggle.setText("Change to Defence");
            } else {
                m_offenceTable.setVisibility(View.GONE);
                m_defenceTable.setVisibility(View.VISIBLE);
                m_bottomTitle.setText("Defending");
                m_offenceToggle.setText("Change to Offence");
            }
        });
    }

    /**
     * Used by loadGrid to update the counts on the cones/cubes
     */
    private void updateCounts() {
        m_topConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.conesTop)));
        m_midConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.conesMid)));
        m_botConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.conesBot)));
        m_topCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.cubesTop)));
        m_midCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.cubesMid)));
        m_botCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.cubesBot)));
    }

    /**
     * Load all the stuff pertaining to the defence table, and set up button listeners
     * @param view The layout for this fragment
     */
    private void loadDefenceTable(View view) {
        m_titleRow = view.findViewById(R.id.title_row);
        m_defenceTable = view.findViewById(R.id.defence_table);
        m_playButtonA = view.findViewById(R.id.play_button_a);
        m_playButtonB = view.findViewById(R.id.play_button_b);
        m_playButtonC = view.findViewById(R.id.play_button_c);
        m_timerA = view.findViewById(R.id.timer_a);
        m_timerB = view.findViewById(R.id.timer_b);
        m_timerC = view.findViewById(R.id.timer_c);
        m_teamNumberA = view.findViewById(R.id.team_number_a);
        m_teamNumberB = view.findViewById(R.id.team_number_b);
        m_teamNumberC = view.findViewById(R.id.team_number_c);
        m_resetButtonA = view.findViewById(R.id.reset_button_a);
        m_resetButtonB = view.findViewById(R.id.reset_button_b);
        m_resetButtonC = view.findViewById(R.id.reset_button_c);

        // Update the title row color to match the team color
        m_titleRow.setBackgroundColor(m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam));

        m_playButtonA.setOnClickListener(v -> {
            m_teamNumberA.clearFocus();
            m_titleRow.requestFocus();
            m_runningDefence[0] = !m_runningDefence[0];
            if (m_runningDefence[0]) {
                m_playButtonA.setImageResource(R.drawable.pause_button);
            } else {
                m_playButtonA.setImageResource(R.drawable.play_button);
            }
        });

        m_playButtonB.setOnClickListener(v -> {
            m_runningDefence[1] = !m_runningDefence[1];
            if (m_runningDefence[1]) {
                m_playButtonB.setImageResource(R.drawable.pause_button);
            } else {
                m_playButtonB.setImageResource(R.drawable.play_button);
            }
        });

        m_playButtonC.setOnClickListener(v -> {
            m_runningDefence[2] = !m_runningDefence[2];
            if (m_runningDefence[2]) {
                m_playButtonC.setImageResource(R.drawable.pause_button);
            } else {
                m_playButtonC.setImageResource(R.drawable.play_button);
            }
        });

        m_resetButtonA.setOnClickListener(v -> {
            m_currentForm.opponentADefenceTime = 0;
            if (m_runningDefence[0]) {
                m_playButtonA.performClick();
            }
        });

        m_resetButtonB.setOnClickListener(v -> {
            m_currentForm.opponentBDefenceTime = 0;
            if (m_runningDefence[1]) {
                m_playButtonB.performClick();
            }
        });

        m_resetButtonC.setOnClickListener(v -> {
            m_currentForm.opponentCDefenceTime = 0;
            if (m_runningDefence[2]) {
                m_playButtonC.performClick();
            }
        });

        // This detects when the enter button is pressed on the EditText field
        // So it can hide the keyboard once the user is done
        // I don't know why it take some much code to do something simple
        // Have to do the same thing on all three team number input fields. yuk
        // I guess it also doubles as a way to set the team number in the scouting form
        m_teamNumberA.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    // Try to parse the team number to an integer
                    try {
                        m_currentForm.opponentA = Integer.parseInt(m_teamNumberA.getText().toString());
                    } catch(Exception e) {
                        m_currentForm.opponentA = 0;
                    }

                    m_teamNumberA.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        m_teamNumberB.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    // Try to parse the team number to an integer
                    try {
                        m_currentForm.opponentB = Integer.parseInt(m_teamNumberB.getText().toString());
                    } catch(Exception e) {
                        m_currentForm.opponentB = 0;
                    }

                    m_teamNumberB.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        m_teamNumberC.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    // Try to parse the team number to an integer
                    try {
                        m_currentForm.opponentC = Integer.parseInt(m_teamNumberC.getText().toString());
                    } catch(Exception e) {
                        m_currentForm.opponentC = 0;
                    }

                    m_teamNumberC.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (m_currentForm.opponentA != 0) {
            String teamNumberA = String.format(Locale.getDefault(), Integer.toString(m_currentForm.opponentA));
            m_teamNumberA.setText(teamNumberA);
        }

        if (m_currentForm.opponentB != 0) {
            String teamNumberB = String.format(Locale.getDefault(), Integer.toString(m_currentForm.opponentB));
            m_teamNumberB.setText(teamNumberB);
        }

        if (m_currentForm.opponentC != 0) {
            String teamNumberC = String.format(Locale.getDefault(), Integer.toString(m_currentForm.opponentC));
            m_teamNumberC.setText(teamNumberC);
        }
    }

    /**
     * Load all the stuff pertaining to the offence table, and set up button listeners
     * @param view The layout for this fragment
     */
    private void loadOffenceTable(View view) {
        m_offenceTable = view.findViewById(R.id.offence_table);
        m_loadingTimer = view.findViewById(R.id.loading_timer);
        m_transportTimer1 = view.findViewById(R.id.transport_timer_1);
        m_transportTimer2 = view.findViewById(R.id.transport_timer_2);
        m_communityTimer = view.findViewById(R.id.community_timer);

        m_transportTimer1.setOnClickListener(v -> {
            // Check if the buttons were pressed in the proper order
            if (m_cyclePhase == -1 || m_cyclePhase == 3) {
                // Check if managed to complete one full cycle
                if (m_cyclePhase == 3) {
                    // Create a new cycle in cycle times
                    m_currentForm.currentCycle++;
                    m_currentForm.cycleTimes.add(m_currentForm.currentCycle, new double[]{0, 0, 0, 0});
                    // Congratulate the user
                    Utilities.showToast(getContext(),"Cycle Completed!",Toast.LENGTH_SHORT);
                }

                // Increment the cycle
                m_cyclePhase = 0;
                // Pause the previous timer and start the current one
                m_runningOffence[3] = false;
                m_runningOffence[0] = true;
                // Highlight the running timer, and deselect the stopped timer
                m_communityTimer.getBackground().clearColorFilter();
                m_transportTimer1.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#777777"), PorterDuff.Mode.MULTIPLY));
            } else {
                // If the buttons were pressed out of order, reset the cycle
                resetCycle();
            }
        });

        m_loadingTimer.setOnClickListener(v -> {
            // Check if the buttons were pressed in the proper order
            if (m_cyclePhase == 0) {
                // Increment the cycle
                m_cyclePhase = 1;
                // Pause the previous timer and start the current one
                m_runningOffence[0] = false;
                m_runningOffence[1] = true;
                // Highlight the running timer, and deselect the stopped timer
                m_transportTimer1.getBackground().clearColorFilter();
                m_loadingTimer.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#777777"), PorterDuff.Mode.MULTIPLY));
            } else {
                // If the buttons were pressed out of order, reset the cycle
                resetCycle();
            }
        });

        m_transportTimer2.setOnClickListener(v -> {
            // Check if the buttons were pressed in the proper order
            if (m_cyclePhase == 1) {
                // Increment the cycle
                m_cyclePhase = 2;
                // Pause the previous timer and start the current one
                m_runningOffence[1] = false;
                m_runningOffence[2] = true;
                // Highlight the running timer, and deselect the stopped timer
                m_loadingTimer.getBackground().clearColorFilter();
                m_transportTimer2.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#777777"), PorterDuff.Mode.MULTIPLY));
            } else {
                // If the buttons were pressed out of order, reset the cycle
                resetCycle();
            }
        });

        m_communityTimer.setOnClickListener(v -> {
            // Check if the buttons were pressed in the proper order
            if (m_cyclePhase == 2) {
                // Increment the cycle
                m_cyclePhase = 3;
                // Pause the previous timer and start the current one
                m_runningOffence[2] = false;
                m_runningOffence[3] = true;
                // Highlight the running timer, and deselect the stopped timer
                m_transportTimer2.getBackground().clearColorFilter();
                m_communityTimer.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#777777"), PorterDuff.Mode.MULTIPLY));
            } else {
                // If the buttons were pressed out of order, reset the cycle
                resetCycle();
            }
        });
    }

    /**
     * Used by offence table to reset the cycle back to phase -1 with timers at 0, and inform the user
     */
    private void resetCycle() {
        Utilities.showToast(getContext(),"Actions Out of Order. Resetting",Toast.LENGTH_SHORT);
        m_cyclePhase = -1;
        m_runningOffence = new boolean[]{false, false, false, false};
        m_currentForm.cycleTimes.set(m_currentForm.currentCycle, new double[]{0, 0, 0, 0});
        m_transportTimer1.getBackground().clearColorFilter();
        m_loadingTimer.getBackground().clearColorFilter();
        m_transportTimer2.getBackground().clearColorFilter();
        m_communityTimer.getBackground().clearColorFilter();
    }


    /**
     * Stopwatch stuff below
     */
    // If the activity is paused, stop the stopwatch.
    @Override
    public void onPause() {
        super.onPause();
        m_runningDefence = new boolean[]{false, false, false};
        m_runningOffence = new boolean[]{false, false, false, false};
    }

    // Sets the Number of seconds on the timer. The runTimer() method uses a Handler
    // to increment the seconds and update the text view.
    private void runTimer() {
        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method, passing in a new Runnable.
        // The post() method processes code without a delay,
        // so the code in the Runnable will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run() {
                // DEFENCE TIMERS:
                // If running is true, increment the seconds variable.
                if (m_runningDefence[0])
                    m_currentForm.opponentADefenceTime+=0.1;
                if (m_runningDefence[1])
                    m_currentForm.opponentBDefenceTime+=0.1;
                if (m_runningDefence[2])
                    m_currentForm.opponentCDefenceTime+=0.1;

                // Format the seconds minutes, and seconds.
                String timerADefenceText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.opponentADefenceTime / 60), (int) Math.floor(m_currentForm.opponentADefenceTime % 60));
                String timerBDefenceText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.opponentBDefenceTime / 60), (int) Math.floor(m_currentForm.opponentBDefenceTime) % 60);
                String timerCDefenceText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.opponentCDefenceTime / 60), (int) Math.floor(m_currentForm.opponentCDefenceTime) % 60);

                // Set the text view text.
                m_timerA.setText(timerADefenceText);
                m_timerB.setText(timerBDefenceText);
                m_timerC.setText(timerCDefenceText);

                // OFFENCE TIMERS:
                // If running is true, increment the seconds variable.
                if (m_runningOffence[0])
                    m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[0]+=0.1;
                if (m_runningOffence[1])
                    m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[1]+=0.1;
                if (m_runningOffence[2])
                    m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[2]+=0.1;
                if (m_runningOffence[3])
                    m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[3]+=0.1;

                // Format the seconds minutes, and seconds.
                String transportTimer1Text = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[0] / 60), (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[0]) % 60);
                String loadingTimerText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[1] / 60), (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[1] % 60));
                String transportTimer2Text = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[2] / 60), (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[2]) % 60);
                String communityTimerText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[3] / 60), (int) Math.floor(m_currentForm.cycleTimes.get(m_currentForm.currentCycle)[3]) % 60);

                // Set the text view text.
                m_transportTimer1.setText(transportTimer1Text);
                m_loadingTimer.setText(loadingTimerText);
                m_transportTimer2.setText(transportTimer2Text);
                m_communityTimer.setText(communityTimerText);

                // Post the code again with a delay of 0.1 second.
                handler.postDelayed(this, 100);
            }
        });
    }
}