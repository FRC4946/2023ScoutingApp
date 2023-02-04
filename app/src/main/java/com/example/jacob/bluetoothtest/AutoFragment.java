package com.example.jacob.bluetoothtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCOUTING_FORM = "scoutingForm";
    private ScoutingForm m_currentForm;
    private Button m_topConeChipMinus, m_topConeChipPlus, m_midConeChipMinus, m_midConeChipPlus, m_botConeChipMinus, m_botConeChipPlus;
    private Button m_topCubeChipMinus, m_topCubeChipPlus, m_midCubeChipMinus, m_midCubeChipPlus, m_botCubeChipMinus, m_botCubeChipPlus;
    private TextView m_topConeCount, m_topCubeCount, m_midConeCount, m_midCubeCount, m_botConeCount, m_botCubeCount;
    private CheckBox m_leftCommunitySwitch, m_dockedSwitch, m_engagedSwitch;
    private TextView m_leftCommunityText, m_dockedText, m_engagedText;

    public AutoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param scoutingForm The current scouting form.
     * @return A new instance of fragment GridFragment.
     */
    public static AutoFragment newInstance(ScoutingForm scoutingForm) {
        AutoFragment fragment = new AutoFragment();
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
        View view = inflater.inflate(R.layout.fragment_auto, container, false);

        m_topConeChipMinus = view.findViewById(R.id.topConeChipMinus);
        m_topConeChipPlus = view.findViewById(R.id.topConeChipPlus);
        m_midConeChipMinus = view.findViewById(R.id.midConeChipMinus);
        m_midConeChipPlus = view.findViewById(R.id.midConeChipPlus);
        m_botConeChipMinus = view.findViewById(R.id.botConeChipMinus);
        m_botConeChipPlus = view.findViewById(R.id.botConeChipPlus);
        m_topCubeChipMinus = view.findViewById(R.id.topCubeChipMinus);
        m_topCubeChipPlus = view.findViewById(R.id.topCubeChipPlus);
        m_midCubeChipMinus = view.findViewById(R.id.midCubeChipMinus);
        m_midCubeChipPlus = view.findViewById(R.id.midCubeChipPlus);
        m_botCubeChipMinus = view.findViewById(R.id.botCubeChipMinus);
        m_botCubeChipPlus = view.findViewById(R.id.botCubeChipPlus);
        m_topConeCount = view.findViewById(R.id.topConeCount);
        m_midConeCount = view.findViewById(R.id.midConeCount);
        m_botConeCount = view.findViewById(R.id.botConeCount);
        m_topCubeCount = view.findViewById(R.id.topCubeCount);
        m_midCubeCount = view.findViewById(R.id.midCubeCount);
        m_botCubeCount = view.findViewById(R.id.botCubeCount);
        m_leftCommunitySwitch = view.findViewById(R.id.left_community_switch);
        m_dockedSwitch = view.findViewById(R.id.docked_switch);
        m_engagedSwitch = view.findViewById(R.id.engaged_switch);
        m_leftCommunityText = view.findViewById(R.id.left_community_text);
        m_dockedText = view.findViewById(R.id.docked_text);
        m_engagedText = view.findViewById(R.id.engaged_text);

        updateCounts();
        updateSwitches();

        m_topConeChipMinus.setOnClickListener(v -> {
            m_currentForm.autoConesTop = Math.max(m_currentForm.autoConesTop - 1, 0);
            updateCounts();
        });

        m_topConeChipPlus.setOnClickListener(v -> {
            m_currentForm.autoConesTop++;
            updateCounts();
        });

        m_midConeChipMinus.setOnClickListener(v -> {
            m_currentForm.autoConesMid = Math.max(m_currentForm.autoConesMid - 1, 0);
            updateCounts();
        });

        m_midConeChipPlus.setOnClickListener(v -> {
            m_currentForm.autoConesMid++;
            updateCounts();
        });

        m_botConeChipMinus.setOnClickListener(v -> {
            m_currentForm.autoConesBot = Math.max(m_currentForm.autoConesBot - 1, 0);
            updateCounts();
        });

        m_botConeChipPlus.setOnClickListener(v -> {
            m_currentForm.autoConesBot++;
            updateCounts();
        });

        m_topCubeChipMinus.setOnClickListener(v -> {
            m_currentForm.autoCubesTop = Math.max(m_currentForm.autoCubesTop - 1, 0);
            updateCounts();
        });

        m_topCubeChipPlus.setOnClickListener(v -> {
            m_currentForm.autoCubesTop++;
            updateCounts();
        });

        m_midCubeChipMinus.setOnClickListener(v -> {
            m_currentForm.autoCubesMid = Math.max(m_currentForm.autoCubesMid - 1, 0);
            updateCounts();
        });

        m_midCubeChipPlus.setOnClickListener(v -> {
            m_currentForm.autoCubesMid++;
            updateCounts();
        });

        m_botCubeChipMinus.setOnClickListener(v -> {
            m_currentForm.autoCubesBot = Math.max(m_currentForm.autoCubesBot - 1, 0);
            updateCounts();
        });

        m_botCubeChipPlus.setOnClickListener(v -> {
            m_currentForm.autoCubesBot++;
            updateCounts();
        });

        m_leftCommunitySwitch.setOnClickListener(v -> {
            m_currentForm.leftCommunity = m_leftCommunitySwitch.isChecked();
        });

        m_dockedSwitch.setOnClickListener(v -> {
            m_currentForm.autoDocked = m_dockedSwitch.isChecked();
        });

        m_engagedSwitch.setOnClickListener(v -> {
            m_currentForm.autoEngaged = m_engagedSwitch.isChecked();
        });

        m_leftCommunityText.setOnClickListener(v -> {
            m_leftCommunitySwitch.performClick();
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

    private void updateCounts() {
        m_topConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.autoConesTop)));
        m_midConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.autoConesMid)));
        m_botConeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.autoConesBot)));
        m_topCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.autoCubesTop)));
        m_midCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.autoCubesMid)));
        m_botCubeCount.setText(String.format(Locale.getDefault(), Integer.toString(m_currentForm.autoCubesBot)));
    }

    private void updateSwitches() {
        m_leftCommunitySwitch.setChecked(m_currentForm.leftCommunity);
        m_dockedSwitch.setChecked(m_currentForm.autoDocked);
        m_engagedSwitch.setChecked(m_currentForm.autoEngaged);
    }
}