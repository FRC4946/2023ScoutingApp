package com.example.jacob.bluetoothtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jacob.bluetoothtest.forms.ScoutingForm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeleopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeleopFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SCOUTING_FORM = "scoutingForm";
    private ScoutingForm m_currentForm;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teleop, container, false);
    }
}