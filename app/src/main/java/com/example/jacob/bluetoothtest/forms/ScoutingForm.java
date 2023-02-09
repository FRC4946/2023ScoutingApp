package com.example.jacob.bluetoothtest.forms;

import com.example.jacob.bluetoothtest.Constants;

import java.io.Serializable;
import java.util.ArrayList;

public class ScoutingForm implements Serializable {
    public Constants.Team team = Constants.Team.RED;

    public int teamNumber = 1;

    public int matchNumber = 0;

    public String scoutName = "";

    public Constants.GameMode currentMode = Constants.GameMode.AUTO;
    public Constants.GameAction currentAction = Constants.GameAction.OFFENCE;
    public Constants.DefenceType currentDefenceType = Constants.DefenceType.IDLE;

    public boolean matchOver = false;
    public boolean matchStarted = false;

    // public TimePeriod climbPeriod = new TimePeriod();
    // public ArrayList<TimePeriod> defenceTimes = new ArrayList<TimePeriod>();
    // public ArrayList<TimePeriod> activeDefenceTimes = new ArrayList<TimePeriod>();

    public boolean leftCommunity = false;
    public int autoConesTop = 0;
    public int autoConesMid = 0;
    public int autoConesBot = 0;
    public int autoCubesTop = 0;
    public int autoCubesMid = 0;
    public int autoCubesBot = 0;
    public int conesTop = 0;
    public int conesMid = 0;
    public int conesBot = 0;
    public int cubesTop = 0;
    public int cubesMid = 0;
    public int cubesBot = 0;
    public boolean autoDocked = false;
    public boolean autoEngaged = false;
    public boolean docked = false;
    public boolean engaged = false;
    public boolean park = false;
    public double endgameTime = 0.0;
    public int opponentA = 0;
    public double opponentADefenceTime = 0;
    public int opponentB = 0;
    public double opponentBDefenceTime = 0;
    public int opponentC = 0;
    public double opponentCDefenceTime = 0;
    public double loadingTime = 0;
    public double transportTime = 0;
    public double communityTime = 0;
    public String startingPosition = "null";
    public int currentCycle = 0; // First cycle will be 0 in the code, but will display 1 to the user

    // The double[] is formatted such that
    // Index 0: Loading Time
    // Index 1: Transport Time
    // Index 2: Community Time
    public ArrayList<double[]> cycleTimes = new ArrayList<>();

    private boolean m_finalized = false;

    public ScoutingForm() {

    }

    public void complete() {
        /*
        if (defenceTimes.size() > 0) {
            defenceTimes.get(defenceTimes.size() - 1).tryEnd();
        }
        defenceTime = TimePeriod.millisToSeconds(getTimeListSum(defenceTimes));
        if (!climbPeriod.ended()) {
            climbPeriod = new TimePeriod();
        }
        climbTime = climbPeriod.getDurationSeconds();
         */
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
                + (leftCommunity ? "True" : "False") + ","
                + autoConesTop + ","
                + autoConesMid + ","
                + autoConesBot + ","
                + autoCubesTop + ","
                + autoCubesMid + ","
                + autoCubesBot + ","
                + conesTop + ","
                + conesMid + ","
                + conesBot + ","
                + cubesTop + ","
                + cubesMid + ","
                + cubesBot + ","
                + (autoDocked ? "True" : "False") + ","
                + (autoEngaged ? "True" : "False") + ","
                + (docked ? "True" : "False") + ","
                + (engaged ? "True" : "False") + ","
                + (park ? "True" : "False") + ","
                + endgameTime + ","
                + opponentA + ","
                + opponentADefenceTime + ","
                + opponentB + ","
                + opponentBDefenceTime + ","
                + opponentC + ","
                + opponentCDefenceTime + ","
                + loadingTime + ","
                + transportTime + ","
                + communityTime + ","
                + startingPosition + ",";
    }

    public static ScoutingForm fromString(String s) {

        String[] arr = s.split(",");

        ScoutingForm ret = new ScoutingForm();

        ret.teamNumber = Integer.parseInt(arr[0]);
        ret.team = Constants.Team.fromString(arr[1]);
        ret.matchNumber = Integer.parseInt(arr[2]);
        ret.scoutName = arr[3];
        ret.leftCommunity = "True".equals(arr[4]);
        ret.autoConesTop = Integer.parseInt(arr[5]);
        ret.autoConesMid = Integer.parseInt(arr[6]);
        ret.autoConesBot = Integer.parseInt(arr[7]);
        ret.autoCubesTop = Integer.parseInt(arr[8]);
        ret.autoCubesMid = Integer.parseInt(arr[9]);
        ret.autoCubesBot = Integer.parseInt(arr[10]);
        ret.conesTop = Integer.parseInt(arr[11]);
        ret.conesMid = Integer.parseInt(arr[12]);
        ret.conesBot = Integer.parseInt(arr[13]);
        ret.cubesTop = Integer.parseInt(arr[14]);
        ret.cubesMid = Integer.parseInt(arr[15]);
        ret.cubesBot = Integer.parseInt(arr[16]);
        ret.autoDocked = "True".equals(arr[17]);
        ret.autoEngaged = "True".equals(arr[18]);
        ret.docked = "True".equals(arr[19]);
        ret.engaged = "True".equals(arr[20]);
        ret.park = "True".equals(arr[21]);
        ret.endgameTime = Double.parseDouble(arr[22]);
        ret.opponentA = Integer.parseInt(arr[23]);
        ret.opponentADefenceTime = Integer.parseInt(arr[24]);
        ret.opponentB = Integer.parseInt(arr[25]);
        ret.opponentBDefenceTime = Integer.parseInt(arr[26]);
        ret.opponentC = Integer.parseInt(arr[27]);
        ret.opponentCDefenceTime = Integer.parseInt(arr[28]);
        ret.matchStarted = true;
        ret.matchOver = true;

        return ret;
    }
}
