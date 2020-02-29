package com.example.jacob.bluetoothtest;

public class Constants {

    public static final int SCOUT_NAME_MAX_UI_LENGTH = 20;

    public static final String START_MATCH_ERROR = "Please Start The Match";
    public static final String MATCH_OVER_ERROR = "Cannot Edit After The Match Has Been Ended";

    public enum GameMode {
        AUTO, TELEOP, ENDGAME
    }

    public enum GameAction {
        OFFENCE, DEFENCE
    }

    public enum DefenceType {
        DEFENDING, IDLE
    }

    public enum Team {
        RED, BLUE;

        @Override
        public String toString() {
            switch (this) {
                case BLUE:
                    return "Blue";
                case RED:
                default:
                    return "Red";
            }
        }
    }

    public enum Climb {
        CLIMB, PARK, NONE;

        @Override
        public String toString() {
            switch (this) {
                case CLIMB:
                    return "Climb";
                case PARK:
                    return "Park";
                case NONE:
                default:
                    return "None";
            }
        }
    }

}
