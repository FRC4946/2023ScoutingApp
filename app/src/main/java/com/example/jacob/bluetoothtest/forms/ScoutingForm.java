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
    // Index 0: Transport Timer 1
    // Index 1: Loading Timer
    // Index 2: Transport Timer 2
    // Index 3: Community Timer
    public ArrayList<double[]> cycleTimes = new ArrayList<>();

    private boolean m_finalized = false;

    public ScoutingForm() {

    }

    public void complete() {
        // If it's finalized already do not recalculate the cycle times
        if (m_finalized) return;

        double[] timeSums = new double[4];

        // Sum all the cycle times
        for (int i = 0; i < cycleTimes.size(); i++) {
            for (int j = 0; j < 4; j++) {
                timeSums[j] += cycleTimes.get(i)[j];
            }
        }

        for (int i = 0; i < 4; i++) {
            System.out.println(timeSums[i]);
        }

        // Store the average cycle times
        loadingTime = timeSums[1] / cycleTimes.size();
        transportTime = (timeSums[0] + timeSums[2]) / 2 / cycleTimes.size();
        communityTime = timeSums[3] / cycleTimes.size();

        // Double check for NaN
        if (Double.isNaN(loadingTime))
            loadingTime = 0;
        if (Double.isNaN(transportTime))
            transportTime = 0;
        if (Double.isNaN(communityTime))
            communityTime = 0;

        m_finalized = true;
    }

    public void setFinalized(boolean finalized) {
        m_finalized = finalized;
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
                + startingPosition;
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
        ret.opponentADefenceTime = Double.parseDouble(arr[24]);
        ret.opponentB = Integer.parseInt(arr[25]);
        ret.opponentBDefenceTime = Double.parseDouble(arr[26]);
        ret.opponentC = Integer.parseInt(arr[27]);
        ret.opponentCDefenceTime = Double.parseDouble(arr[28]);
        ret.loadingTime = Double.parseDouble(arr[29]);
        ret.transportTime = Double.parseDouble(arr[30]);
        ret.communityTime = Double.parseDouble(arr[31]);
        ret.startingPosition = arr[32];
        ret.matchStarted = true;
        ret.matchOver = true;

        return ret;
    }
}
