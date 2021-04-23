package com.dmv.footballheadz.game.impl;

import java.util.List;

public class HeadToHead {

    String team1;
    String team2;
    String record;
    List<Game> games;

    public HeadToHead(String team1, String team2, List<Game> games, int homeWins) {
        this.team1 = team1;
        this.team2 = team2;
        this.games = games;
        this.record = buildRecordString(homeWins);
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    private String buildRecordString(int homeWins) {
        StringBuilder sb = new StringBuilder(team1);
        sb.append(" : ");
        sb.append(homeWins);
        sb.append(" - ");
        sb.append(team2);
        sb.append(" : ");
        sb.append(games.size() - homeWins);
        return sb.toString();
    }
}
