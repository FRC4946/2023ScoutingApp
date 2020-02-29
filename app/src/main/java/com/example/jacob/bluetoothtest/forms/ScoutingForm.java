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
    public Constants.DefenceType currentDefenceType = Constants.DefenceType.IDLE;

    public boolean matchOver = false;
    public boolean matchStarted = false;

    public TimePeriod climbPeriod = new TimePeriod();
    public ArrayList<TimePeriod> defenceTimes = new ArrayList<TimePeriod>();
    public ArrayList<TimePeriod> activeDefenceTimes = new ArrayList<TimePeriod>();

    public double defenceTime = 0;
    public double activeDefenceTime = 0;
    public double climbTime = 0;

    public int trenchBalls = 0;
    public int trenchBallsShot = 0;
    public int fieldBalls = 0;
    public int fieldBallsShot = 0;
    public int targetBalls = 0;
    public int targetBallsShot = 0;

    public int autoBalls = 0;
    public int autoBallsShot = 0;

    public Constants.Climb climb = Constants.Climb.NONE;

    private boolean m_finalized = false;

    public ScoutingForm() {

    }

    public void complete() {
        if (defenceTimes.size() > 0) {
            defenceTimes.get(defenceTimes.size() - 1).tryEnd();
        }
        if (activeDefenceTimes.size() > 0) {
            activeDefenceTimes.get(activeDefenceTimes.size() - 1).tryEnd();
        }
        defenceTime = TimePeriod.millisToSeconds(getTimeListSum(defenceTimes));
        activeDefenceTime = TimePeriod.millisToSeconds(getTimeListSum(activeDefenceTimes));
        if (!climbPeriod.ended()) {
            climbPeriod = new TimePeriod();
        }
        climbTime = climbPeriod.getDurationSeconds();
        m_finalized = true;
    }

    public boolean getCompleted() {
        return m_finalized;
    }

    @Override
    public String toString() {
        return teamNumber + ","
                + team + ","
                + matchNumber + ","
                + scoutName + ","
                + (crossedAutoLine ? "True" : "False") + ","
                + autoBalls + ","
                + autoBallsShot + ","
                + trenchBalls + ","
                + trenchBallsShot + ","
                + fieldBalls + ","
                + fieldBallsShot + ","
                + targetBalls + ","
                + targetBallsShot + ","
                + activeDefenceTime + ","
                + defenceTime + ","
                + climbTime + ","
                + climb;
    }

    public static ScoutingForm fromString(String s) {

        String[] arr = s.split(",");

        ScoutingForm ret = new ScoutingForm();

        ret.teamNumber = Integer.parseInt(arr[0]);
        ret.team = Constants.Team.fromString(arr[1]);
        ret.matchNumber = Integer.parseInt(arr[2]);
        ret.scoutName = arr[3];
        ret.crossedAutoLine = "True".equals(arr[4]);
        ret.autoBalls = Integer.parseInt(arr[5]);
        ret.autoBallsShot = Integer.parseInt(arr[6]);
        ret.trenchBalls = Integer.parseInt(arr[7]);
        ret.trenchBallsShot = Integer.parseInt(arr[8]);
        ret.fieldBalls = Integer.parseInt(arr[9]);
        ret.fieldBallsShot = Integer.parseInt(arr[10]);
        ret.targetBalls = Integer.parseInt(arr[11]);
        ret.targetBallsShot = Integer.parseInt(arr[12]);
        ret.activeDefenceTime = Double.parseDouble(arr[13]);
        ret.defenceTime = Double.parseDouble(arr[14]);
        ret.climbTime = Double.parseDouble(arr[15]);
        ret.climb = Constants.Climb.fromString(arr[16]);

        ret.matchStarted = true;
        ret.matchOver = true;

        return ret;
    }

    private long getTimeListSum(ArrayList<TimePeriod> list) {
        long sum = 0;
        for (TimePeriod p : list) {
            sum += p.getDuration();
        }
        return sum;
    }
}
