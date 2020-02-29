package com.example.jacob.bluetoothtest.forms;

import com.example.jacob.bluetoothtest.Constants;

import java.util.ArrayList;

public class ScoutingForm {
    public boolean crossedAutoLine = false;

    public Constants.Team team = Constants.Team.RED;

    public int teamNumber = 1;

    public int matchNumber = 0;

    public String scoutName = "";

    public Constants.GameMode currentMode = Constants.GameMode.AUTO;
    public Constants.GameAction currentAction = Constants.GameAction.OFFENCE;
    public Constants.DefenceType currentDefenceType = Constants.DefenceType.DEFENDING;

    public boolean matchOver = false;
    public boolean matchStarted = false;

    public TimePeriod m_matchPeriod = new TimePeriod();

    public TimePeriod climbTime = new TimePeriod();
    public ArrayList<TimePeriod> defenceTimes = new ArrayList<TimePeriod>();
    public ArrayList<TimePeriod> activeDefenceTimes = new ArrayList<TimePeriod>();

    public long defenceTime = 0;
    public long activeDefenceTime = 0;

    public int trenchBalls = 0;
    public int trenchBallsShot = 0;
    public int fieldBalls = 0;
    public int fieldBallsShot = 0;
    public int targetBalls = 0;
    public int targetBallsShot = 0;

    public int autoBalls = 0;
    public int autoBallsShot = 0;

    public boolean attemptedClimb = false;
    public Constants.Climb climb = Constants.Climb.NONE;

    private boolean m_finalized = false;

    public ScoutingForm() {

    }

    public void finalize() {
        defenceTime = getTimeListSum(defenceTimes);
        activeDefenceTime = getTimeListSum(activeDefenceTimes);
        m_finalized = true;
    }

    public boolean getFinalized() {
        return m_finalized;
    }

    @Override
    public String toString() {
        return "";
    }

    public static ScoutingForm fromString(String s) {
        return new ScoutingForm();
    }

    private long getTimeListSum(ArrayList<TimePeriod> list) {
        long sum = 0;
        for (TimePeriod p : list) {
            sum += p.getDuration();
        }
        return sum;
    }
}
