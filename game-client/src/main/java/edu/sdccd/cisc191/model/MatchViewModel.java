package edu.sdccd.cisc191.model;

import java.util.concurrent.atomic.AtomicInteger;

public class MatchViewModel {

    private String matchId;

    private final Player player = new Player("Player");
    private final Player opponent = new Player("Opponent");

    private boolean matchOver;
    private String winnerName = "";

    private final AtomicInteger completedMatchCount = new AtomicInteger(0);

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {
        return opponent;
    }

    public boolean isMatchOver() {
        return matchOver;
    }

    public void setMatchOver(boolean matchOver) {
        this.matchOver = matchOver;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = (winnerName == null) ? "" : winnerName;
    }

    public int getCompletedMatchCount() {
        return completedMatchCount.get();
    }

    /**
     * Thread-safe update for completed matches.
     */
    public synchronized void recordCompletedMatchThreadSafely(String winnerName) {
        completedMatchCount.incrementAndGet();
        setWinnerName(winnerName);
        matchOver = true;
    }

    public boolean hasJoinedMatch() {
        return matchId != null && !matchId.isBlank();
    }

    public boolean canPlayMatch() {
        return hasJoinedMatch() && !matchOver;
    }

    /**
     * Build match summary for UI.
     */
    public String buildMatchSummary(String difficulty, boolean ranked) {

        if (matchId == null || matchId.isBlank()) {
            return "No match";
        }

        String resolvedDifficulty =
                (difficulty == null || difficulty.isBlank()) ? "Normal" : difficulty.trim();

        String rankedLabel = ranked ? "ranked" : "casual";

        return "Match " + matchId + ": "
                + player.getName()
                + " vs "
                + opponent.getName()
                + " (" + resolvedDifficulty + ", " + rankedLabel + ")";
    }

    public void resetLocalState() {
        matchId = null;
        player.setName("Player");
        opponent.setName("Opponent");
        matchOver = false;
        winnerName = "";
        completedMatchCount.set(0);
    }
}