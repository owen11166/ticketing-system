package com.owen.ticketingsystem.entity;

import java.io.IOException;

public class Match {
    private String date;
    private String time;
    private String teams;
    private String location;

    public Match() {

    }

    public Match(String date, String time, String teams, String location) {
        this.date = date;
        this.time = time;
        this.teams = teams;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeams() {
        return teams;
    }

    public void setTeams(String teams) {
        this.teams = teams;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
