package com.example.jacob.bluetoothtest;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EndgameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndgameFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCOUTING_FORM = "scoutingForm";
    private ScoutingForm m_currentForm;
    private TextView m_endgameTimer;
    private ImageButton m_playButton, m_resetButton;
    private CheckBox m_parkedSwitch, m_dockedSwitch, m_engagedSwitch;
    private TextView m_parkedText, m_dockedText, m_engagedText;
    private ImageView m_chargingStationImage;
    private boolean m_running; // Whether or not the endgame timer is running

    public EndgameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param scoutingForm The current scouting form.
     * @return A new instance of fragment EndgameFragment.
     */
    public static EndgameFragment newInstance(ScoutingForm scoutingForm) {
        EndgameFragment fragment = new EndgameFragment();
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

        View view = inflater.inflate(R.layout.fragment_endgame, container, false);

        m_endgameTimer = view.findViewById(R.id.endgame_timer);
        m_playButton = view.findViewById(R.id.play_button);
        m_resetButton = view.findViewById(R.id.reset_button);
        m_parkedSwitch = view.findViewById(R.id.parked_switch);
        m_dockedSwitch = view.findViewById(R.id.docked_switch);
        m_engagedSwitch = view.findViewById(R.id.engaged_switch);
        m_parkedText = view.findViewById(R.id.parked_text);
        m_dockedText = view.findViewById(R.id.docked_text);
        m_engagedText = view.findViewById(R.id.engaged_text);
        m_chargingStationImage = view.findViewById(R.id.charging_station_image);

        runTimer();
        updateSwitches();
        updateChargingStationImage();

        m_playButton.setOnClickListener(v -> {
            m_running = !m_running;
            if (m_running) {
                m_playButton.setImageResource(R.drawable.pause_button);
            } else {
                m_playButton.setImageResource(R.drawable.play_button);
            }
        });

        m_resetButton.setOnClickListener(v -> {
            m_currentForm.endgameTime = 0;
            if (m_running) {
                m_playButton.performClick();
            }
        });

        m_parkedSwitch.setOnClickListener(v -> {
            m_currentForm.park = m_parkedSwitch.isChecked();
            // Automatically turn off docked and engaged if park is on
            if (m_parkedSwitch.isChecked() && m_dockedSwitch.isChecked()) {
                m_dockedSwitch.performClick();
            }
            if (m_parkedSwitch.isChecked() && m_engagedSwitch.isChecked()) {
                m_engagedText.performClick();
            }
            updateChargingStationImage();
        });

        m_dockedSwitch.setOnClickListener(v -> {
            m_currentForm.docked = m_dockedSwitch.isChecked();
            // Automatically turn off parked if docked is on
            if (m_dockedSwitch.isChecked() && m_parkedSwitch.isChecked()) {
                m_parkedSwitch.performClick();
            }
            updateChargingStationImage();
        });

        m_engagedSwitch.setOnClickListener(v -> {
            m_currentForm.engaged = m_engagedSwitch.isChecked();
            // Automatically turn on docked if engaged is on
            if (m_engagedSwitch.isChecked() && !m_dockedSwitch.isChecked()) {
                m_dockedSwitch.performClick();
            }
            updateChargingStationImage();
        });

        m_parkedText.setOnClickListener(v -> {
            m_parkedSwitch.performClick();
        });

        m_dockedText.setOnClickListener(v -> {
            m_dockedSwitch.performClick();
        });

        m_engagedText.setOnClickListener(v -> {
            m_engagedSwitch.performClick();
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void updateSwitches() {
        m_parkedSwitch.setChecked(m_currentForm.park);
        m_dockedSwitch.setChecked(m_currentForm.docked);
        m_engagedSwitch.setChecked(m_currentForm.engaged);
    }

    private void updateChargingStationImage() {
        if (m_currentForm.engaged) {
            m_chargingStationImage.setVisibility(View.VISIBLE);
            m_chargingStationImage.setImageResource(R.drawable.engaged);
        } else if (m_currentForm.docked) {
            m_chargingStationImage.setVisibility(View.VISIBLE);
            m_chargingStationImage.setImageResource(R.drawable.docked);
        } else if (m_currentForm.park) {
            m_chargingStationImage.setVisibility(View.VISIBLE);
            m_chargingStationImage.setImageResource(R.drawable.parked);
        } else {
            m_chargingStationImage.setVisibility(View.GONE);
        }
    }

    /**
     * Stopwatch stuff below
     */
    // If the activity is paused, stop the stopwatch.
    @Override
    public void onPause() {
        super.onPause();
        m_running = false;
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
                // If running is true, increment the seconds variable.
                if (m_running)
                    m_currentForm.endgameTime += 0.1;

                // Format the seconds minutes, and seconds.
                String endgameTimerText = String.format(Locale.getDefault(), "%d:%02d", (int) Math.floor(m_currentForm.endgameTime / 60), (int) Math.floor(m_currentForm.endgameTime) % 60);

                // Set the text view text.
                m_endgameTimer.setText(endgameTimerText);

                // Post the code again with a delay of 0.1 second.
                handler.postDelayed(this, 100);
            }
        });
    }
}