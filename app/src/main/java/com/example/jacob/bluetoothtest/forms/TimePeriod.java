package com.example.jacob.bluetoothtest.forms;

public class TimePeriod {

    private long m_startTime = 0;
    private long m_endTime = 0;

    public void start() {
        m_startTime = System.currentTimeMillis();
    }

    public void end() {
        m_endTime = System.currentTimeMillis();
    }

    public void tryEnd() {
        if (m_endTime == 0)
            end();
    }

    public long getDuration() {
        return m_endTime - m_startTime;
    }

    public double getDurationSeconds() {
        return ((double) getDuration()) / 1000d;
    }

    public static double millisToSeconds(long millis) {
        return ((double) millis) / 1000d;
    }

    public double getDurationMinutes() {
        return getDurationSeconds() / 60d;
    }

    public long getStartTime() {
        return m_startTime;
    }

    public long getEndTime() {
        return m_endTime;
    }

    public boolean started() {
        return m_startTime != 0;
    }

    public boolean ended() {
        return m_endTime != 0;
    }

    @Override
    public String toString() {
        int duration = (int) getDurationSeconds();
        return "" + duration / 60 + "m : " + duration % 60 + "s";
    }

}
