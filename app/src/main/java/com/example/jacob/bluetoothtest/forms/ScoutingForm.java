package com.example.jacob.bluetoothtest.forms;

import com.example.jacob.bluetoothtest.Constants;

import java.util.ArrayList;

public class ScoutingForm {
    public boolean crossedAutoLine = false;

    public Constants.Team team = Constants.Team.RED;
    public int driverStation = 1;

    public int matchNumber = 0;

    public String scoutName = "";

    public Constants.GameMode currentMode = Constants.GameMode.AUTO;
    public Constants.GameAction currentAction = Constants.GameAction.OFFENCE;
    public Constants.DefenceType currentDefenceType = Constants.DefenceType.DEFENDING;

    public boolean matchOver = false;
    public boolean matchStarted = false;

    public TimePeriod m_matchPeriod = new TimePeriod();

    public ArrayList<TimePeriod> climbTimes = new ArrayList<TimePeriod>();
    public ArrayList<TimePeriod> defenceTimes = new ArrayList<TimePeriod>();
    public ArrayList<TimePeriod> activeDefenceTimes = new ArrayList<TimePeriod>();

    public long climbTime = 0;
    public long defenceTime = 0;
    public long activeDefenceTime = 0;

    public int teleopHighBalls = 0;
    public int teleopHighBallsShot = 0;
    public int teleopLowBalls = 0;
    public int teleopLowBallsShot = 0;

    public int autoHighBalls = 0;
    public int autoHighBallsShot = 0;
    public int autoLowBalls = 0;
    public int autoLowBallsShot = 0;

    public int loadingStationIntake = 0;
    public int loadingStationIntakeAttempt = 0;

    public boolean attemptedStage2 = false;
    public boolean successStage2 = false;
    public boolean attemptedStage3 = false;
    public boolean successStage3 = false;

    public boolean attemptedClimb = false;
    public boolean climbed = false;

    private boolean m_finalized = false;

    public ScoutingForm() {

    }

    public void finalize() {
        climbTime = getTimeListSum(climbTimes);
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
