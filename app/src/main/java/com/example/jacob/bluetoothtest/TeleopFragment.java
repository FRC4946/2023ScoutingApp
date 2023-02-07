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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

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
    private ImageButton m_playButtonA, m_playButtonB, m_playButtonC, m_resetButtonA, m_resetButtonB, m_resetButtonC;
    private TextView m_timerA, m_timerB, m_timerC;
    private TableRow m_titleRow;
    private EditText m_teamNumberA, m_teamNumberB, m_teamNumberC;
    private float[] m_seconds = {0.0f, 0.0f, 0.0f};
    private boolean[] m_running = {false, false, false};

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

        m_seconds[0] = m_currentForm.opponentADefenceTime;
        m_seconds[1] = m_currentForm.opponentBDefenceTime;
        m_seconds[2] = m_currentForm.opponentCDefenceTime;

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
        m_playButtonA = view.findViewById(R.id.play_button_a);
        m_playButtonB = view.findViewById(R.id.play_button_b);
        m_playButtonC = view.findViewById(R.id.play_button_c);
        m_timerA = view.findViewById(R.id.timer_a);
        m_timerB = view.findViewById(R.id.timer_b);
        m_timerC = view.findViewById(R.id.timer_c);
        m_titleRow = view.findViewById(R.id.title_row);
        m_teamNumberA = view.findViewById(R.id.team_number_a);
        m_teamNumberB = view.findViewById(R.id.team_number_b);
        m_teamNumberC = view.findViewById(R.id.team_number_c);
        m_resetButtonA = view.findViewById(R.id.reset_button_a);
        m_resetButtonB = view.findViewById(R.id.reset_button_b);
        m_resetButtonC = view.findViewById(R.id.reset_button_c);

        updateCounts();
        runTimer();
        updateTitleRowColor();

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

        m_playButtonA.setOnClickListener(v -> {
            m_teamNumberA.clearFocus();
            m_titleRow.requestFocus();
            m_running[0] = !m_running[0];
            if (m_running[0]) {
                m_playButtonA.setImageResource(R.drawable.pause_button);
            } else {
                m_playButtonA.setImageResource(R.drawable.play_button);
            }
        });

        m_playButtonB.setOnClickListener(v -> {
            m_running[1] = !m_running[1];
            if (m_running[1]) {
                m_playButtonB.setImageResource(R.drawable.pause_button);
            } else {
                m_playButtonB.setImageResource(R.drawable.play_button);
            }
        });

        m_playButtonC.setOnClickListener(v -> {
            m_running[2] = !m_running[2];
            if (m_running[2]) {
                m_playButtonC.setImageResource(R.drawable.pause_button);
            } else {
                m_playButtonC.setImageResource(R.drawable.play_button);
            }
        });

        m_resetButtonA.setOnClickListener(v -> {
            m_seconds[0] = 0;
            m_running[0] = false;
        });

        m_resetButtonB.setOnClickListener(v -> {
            m_seconds[1] = 0;
            m_running[1] = false;
        });

        m_resetButtonC.setOnClickListener(v -> {
            m_seconds[2] = 0;
            m_running[2] = false;
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
                    m_currentForm.opponentA = Integer.parseInt(m_teamNumberA.getText().toString());
                    System.out.println(m_currentForm.opponentA);
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
                    m_currentForm.opponentB = Integer.parseInt(m_teamNumberB.getText().toString());
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
                    m_currentForm.opponentC = Integer.parseInt(m_teamNumberC.getText().toString());
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

        // Inflate the layout for this fragment
        return view;
    }

    // If the activity is paused, stop the stopwatch.
    @Override
    public void onPause() {
        super.onPause();
        m_running = new boolean[]{false, false, false};
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
                // Format the seconds minutes, and seconds.
                String timerAText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_seconds[0] / 60), (int) Math.floor(m_seconds[0]) % 60);
                String timerBText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_seconds[1] / 60), (int) Math.floor(m_seconds[1]) % 60);
                String timerCText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_seconds[2] / 60), (int) Math.floor(m_seconds[2]) % 60);

                // Set the text view text.
                m_timerA.setText(timerAText);
                m_timerB.setText(timerBText);
                m_timerC.setText(timerCText);

                // If running is true, increment the seconds variable.
                for (int i = 0; i < 3; i++) {
                    if (m_running[i]) {
                        m_seconds[i]+=0.1;
                    }
                }

                // Post the code again with a delay of 0.1 second.
                handler.postDelayed(this, 100);
            }
        });
    }

    private void updateCounts() {
        m_topConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.conesTop)));
        m_midConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.conesMid)));
        m_botConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.conesBot)));
        m_topCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.cubesTop)));
        m_midCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.cubesMid)));
        m_botCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.cubesBot)));
    }

    private void updateTitleRowColor() {
        m_titleRow.setBackgroundColor(m_currentForm.team == Constants.Team.RED ? getResources().getColor(R.color.redTeam) : getResources().getColor(R.color.blueTeam));
    }
}