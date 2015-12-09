/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.util.Comparator;

public class CompetitionParticipant {
    private static Comparator avgWeightedComparator;
    private static Comparator avgComparator;
    private static Comparator minAvgZeroComparator;
    private static Comparator minAvgZeroWeightedComparator;
    private int id;
    private int parentID;
    private String name;
    private int flags;
    private double totalScore;
    private double wTotalScore;
    private int gamesPlayed;
    private int zGamesPlayed;
    private double wGamesPlayed;
    private double zwGamesPlayed;
    private double avgScore1;
    private double avgScore2;
    private double avgScore3;
    private double avgScore4;

    public static Comparator getAvgWeightedComparator() {
        if (avgWeightedComparator == null) {
            avgWeightedComparator = new Comparator(){

                public int compare(Object o1, Object o2) {
                    double diff = ((CompetitionParticipant)o2).getAvgWeightedScore() - ((CompetitionParticipant)o1).getAvgWeightedScore();
                    return diff < 0.0 ? -1 : (diff > 0.0 ? 1 : 0);
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return avgWeightedComparator;
    }

    public static Comparator getAvgComparator() {
        if (avgComparator == null) {
            avgComparator = new Comparator(){

                public int compare(Object o1, Object o2) {
                    double diff = ((CompetitionParticipant)o2).getAvgScore() - ((CompetitionParticipant)o1).getAvgScore();
                    return diff < 0.0 ? -1 : (diff > 0.0 ? 1 : 0);
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return avgComparator;
    }

    public static Comparator getMinAvgZeroComparator() {
        if (minAvgZeroComparator == null) {
            minAvgZeroComparator = new Comparator(){

                public int compare(Object o1, Object o2) {
                    double diff;
                    CompetitionParticipant cp1 = (CompetitionParticipant)o1;
                    CompetitionParticipant cp2 = (CompetitionParticipant)o2;
                    double avg1 = cp1.getAvgScore();
                    double avgzero1 = cp1.getAvgScoreWithoutZeroGames();
                    double avg2 = cp2.getAvgScore();
                    double avgzero2 = cp2.getAvgScoreWithoutZeroGames();
                    if (avgzero1 < avg1) {
                        avg1 = avgzero1;
                    }
                    if (avgzero2 < avg2) {
                        avg2 = avgzero2;
                    }
                    return (diff = avg2 - avg1) < 0.0 ? -1 : (diff > 0.0 ? 1 : 0);
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return minAvgZeroComparator;
    }

    public static Comparator getMinAvgZeroWeightedComparator() {
        if (minAvgZeroWeightedComparator == null) {
            minAvgZeroWeightedComparator = new Comparator(){

                public int compare(Object o1, Object o2) {
                    double diff;
                    CompetitionParticipant cp1 = (CompetitionParticipant)o1;
                    CompetitionParticipant cp2 = (CompetitionParticipant)o2;
                    double avg1 = cp1.getAvgWeightedScore();
                    double avgzero1 = cp1.getAvgWeightedScoreWithoutZeroGames();
                    double avg2 = cp2.getAvgWeightedScore();
                    double avgzero2 = cp2.getAvgWeightedScoreWithoutZeroGames();
                    if (avgzero1 < avg1) {
                        avg1 = avgzero1;
                    }
                    if (avgzero2 < avg2) {
                        avg2 = avgzero2;
                    }
                    return (diff = avg2 - avg1) < 0.0 ? -1 : (diff > 0.0 ? 1 : 0);
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj == this) {
                        return true;
                    }
                    return false;
                }
            };
        }
        return minAvgZeroWeightedComparator;
    }

    public CompetitionParticipant(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CompetitionParticipant(CompetitionParticipant user) {
        this.id = user.id;
        this.name = user.name;
        this.parentID = user.parentID;
        this.flags = user.flags;
        this.addScore(user);
    }

    public int getID() {
        return this.id;
    }

    public boolean hasParent() {
        if (this.parentID >= 0) {
            return true;
        }
        return false;
    }

    public int getParent() {
        return this.parentID;
    }

    void setParent(int parentID) {
        this.parentID = parentID;
    }

    public String getName() {
        return this.name;
    }

    public int getFlags() {
        return this.flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public double getTotalScore() {
        return this.totalScore;
    }

    public double getAvgScore() {
        return this.gamesPlayed == 0 ? 0.0 : this.totalScore / (double)this.gamesPlayed;
    }

    public double getAvgScoreWithoutZeroGames() {
        int games = this.gamesPlayed - this.zGamesPlayed;
        if (games <= 0) {
            return 0.0;
        }
        return this.totalScore / (double)games;
    }

    public double getTotalWeightedScore() {
        return this.wTotalScore;
    }

    public double getAvgWeightedScore() {
        return this.wGamesPlayed == 0.0 ? 0.0 : this.wTotalScore / this.wGamesPlayed;
    }

    public double getAvgWeightedScoreWithoutZeroGames() {
        double wgames = this.wGamesPlayed - this.zwGamesPlayed;
        return wgames <= 0.0 ? 0.0 : this.wTotalScore / wgames;
    }

    public int getGamesPlayed() {
        return this.gamesPlayed;
    }

    public int getZeroGamesPlayed() {
        return this.zGamesPlayed;
    }

    public double getWeightedGamesPlayed() {
        return this.wGamesPlayed;
    }

    public double getZeroWeightedGamesPlayed() {
        return this.zwGamesPlayed;
    }

    public double getAvgScore1() {
        return this.avgScore1;
    }

    public double getAvgScore2() {
        return this.avgScore2;
    }

    public double getAvgScore3() {
        return this.avgScore3;
    }

    public double getAvgScore4() {
        return this.avgScore4;
    }

    public void addScore(CompetitionParticipant user) {
        this.totalScore += user.totalScore;
        this.wTotalScore += user.wTotalScore;
        this.gamesPlayed += user.gamesPlayed;
        this.zGamesPlayed += user.zGamesPlayed;
        this.wGamesPlayed += user.wGamesPlayed;
        this.zwGamesPlayed += user.zwGamesPlayed;
    }

    public void addScore(int simulationID, double score, double weight, boolean isZeroGame) {
        this.totalScore += score;
        this.wTotalScore += score * weight;
        ++this.gamesPlayed;
        this.wGamesPlayed += weight;
        if (isZeroGame) {
            ++this.zGamesPlayed;
            this.zwGamesPlayed += weight;
        }
    }

    public void removeScore(int simulationID, double score, double weight, boolean isZeroGame) {
        this.totalScore -= score;
        this.wTotalScore -= score * weight;
        --this.gamesPlayed;
        this.wGamesPlayed -= weight;
        if (isZeroGame) {
            --this.zGamesPlayed;
            this.zwGamesPlayed -= weight;
        }
    }

    void setScores(double totalScore, double wTotalScore, int gamesPlayed, int zGamesPlayed, double wGamesPlayed, double zwGamesPlayed) {
        this.totalScore = totalScore;
        this.wTotalScore = wTotalScore;
        this.gamesPlayed = gamesPlayed;
        this.zGamesPlayed = zGamesPlayed;
        this.wGamesPlayed = wGamesPlayed;
        this.zwGamesPlayed = zwGamesPlayed;
    }

    void setAvgScores(double a1, double a2, double a3, double a4) {
        this.avgScore1 = a1;
        this.avgScore2 = a2;
        this.avgScore3 = a3;
        this.avgScore4 = a4;
    }

    void clearScores() {
        this.totalScore = 0.0;
        this.wTotalScore = 0.0;
        this.gamesPlayed = 0;
        this.zGamesPlayed = 0;
        this.wGamesPlayed = 0.0;
        this.zwGamesPlayed = 0.0;
        this.avgScore1 = 0.0;
        this.avgScore2 = 0.0;
        this.avgScore3 = 0.0;
        this.avgScore4 = 0.0;
    }

    public static int indexOf(CompetitionParticipant[] participants, int start, int end, int userID) {
        int i = start;
        while (i < end) {
            if (participants[i].id == userID) {
                return i;
            }
            ++i;
        }
        return -1;
    }

}

