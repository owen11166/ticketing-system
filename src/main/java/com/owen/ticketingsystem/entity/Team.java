package com.owen.ticketingsystem.entity;

public class Team {
    private int rank;
    private String name;
    private int played;
    private String record;
    private double winRate;
    private double gamesBehind;
    private String streak;

    public Team() {
    }
    public Team(int rank, String name, int played, String record, double winRate, double gamesBehind, String streak) {
        this.rank = rank;
        this.name = name;
        this.played = played;
        this.record = record;
        this.winRate = winRate;
        this.gamesBehind = gamesBehind;
        this.streak = streak;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getGamesBehind() {
        return gamesBehind;
    }

    public void setGamesBehind(double gamesBehind) {
        this.gamesBehind = gamesBehind;
    }

    public String getStreak() {
        return streak;
    }

    public void setStreak(String streak) {
        this.streak = streak;
    }
}
