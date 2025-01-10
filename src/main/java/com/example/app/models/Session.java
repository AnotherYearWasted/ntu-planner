package com.example.app.models;

import java.time.DayOfWeek;

public class Session {

    private DayOfWeek day;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public Session() {

    }

    public Session(DayOfWeek day, int startHour, int startMinute, int endHour, int endMinute) {
        this.day = day;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public boolean isOverlap(Session other) {
        if (this.day != other.day) {
            return false;
        }

        if (this.startHour > other.endHour || this.endHour < other.startHour) {
            return false;
        }

        if (this.startHour == other.endHour && this.startMinute >= other.endMinute) {
            return false;
        }

        return this.endHour != other.startHour || this.endMinute > other.startMinute;
    }

    public boolean isOverlap(int startHour, int startMinute, int endHour, int endMinute) {
        return isOverlap(new Session(day, startHour, startMinute, endHour, endMinute));
    }
}
