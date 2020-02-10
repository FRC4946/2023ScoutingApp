package com.example.jacob.bluetoothtest;

public class Constants {

    public static final int SCOUT_NAME_MAX_UI_LENGTH = 20;

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

}
