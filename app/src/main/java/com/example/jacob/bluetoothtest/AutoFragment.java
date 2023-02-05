package com.example.jacob.bluetoothtest;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private ImageButton m_topCone, m_midCone, m_botCone, m_topCube, m_midCube, m_botCube;
    private CheckBox m_deleteModeSwitch;
    private TextView m_topConeCount, m_topCubeCount, m_midConeCount, m_midCubeCount, m_botConeCount, m_botCubeCount;
    private CheckBox m_leftCommunitySwitch, m_dockedSwitch, m_engagedSwitch;
    private TextView m_leftCommunityText, m_dockedText, m_engagedText;
    private LinearLayout m_topRowBorder, m_midRowBorder, m_botRowBorder;

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
        m_leftCommunitySwitch = view.findViewById(R.id.left_community_switch);
        m_dockedSwitch = view.findViewById(R.id.docked_switch);
        m_engagedSwitch = view.findViewById(R.id.engaged_switch);
        m_leftCommunityText = view.findViewById(R.id.left_community_text);
        m_dockedText = view.findViewById(R.id.docked_text);
        m_engagedText = view.findViewById(R.id.engaged_text);
        m_topRowBorder = view.findViewById(R.id.top_row_border);
        m_midRowBorder = view.findViewById(R.id.mid_row_border);
        m_botRowBorder = view.findViewById(R.id.bot_row_border);

        updateCounts();
        updateSwitches();

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
                m_currentForm.autoConesTop = Math.max(m_currentForm.autoConesTop - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.autoConesTop = Math.min(m_currentForm.autoConesTop + 1, 6);
            }
            updateCounts();
        });

        m_midCone.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.autoConesMid = Math.max(m_currentForm.autoConesMid - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.autoConesMid = Math.min(m_currentForm.autoConesMid + 1, 6);
            }
            updateCounts();
        });

        m_botCone.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.autoConesBot = Math.max(m_currentForm.autoConesBot - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.autoConesBot = Math.min(m_currentForm.autoConesBot + 1, 9);
            }
            updateCounts();
        });

        m_topCube.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.autoCubesTop = Math.max(m_currentForm.autoCubesTop - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.autoCubesTop = Math.min(m_currentForm.autoCubesTop + 1, 3);
            }
            updateCounts();
        });

        m_midCube.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.autoCubesMid = Math.max(m_currentForm.autoCubesMid - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.autoCubesMid = Math.min(m_currentForm.autoCubesMid + 1, 3);
            }
            updateCounts();
        });

        m_botCube.setOnClickListener(v -> {
            if (m_deleteModeSwitch.isChecked()) {
                m_currentForm.autoCubesBot = Math.max(m_currentForm.autoCubesBot - 1, 0);
                m_deleteModeSwitch.performClick();
            } else {
                m_currentForm.autoCubesBot = Math.min(m_currentForm.autoCubesBot + 1, 9);
            }
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
            if (m_engagedSwitch.isChecked()) {
                m_dockedSwitch.setChecked(true);
            }
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