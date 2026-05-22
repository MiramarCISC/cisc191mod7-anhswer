package edu.sdccd.cisc191.server;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks server-wide match statistics shared by multiple gRPC request threads.
 */
public class MatchStatistics {

    private final AtomicInteger joinedMatchCount = new AtomicInteger(0);
    private final AtomicInteger completedMatchCount = new AtomicInteger(0);

    /** Thread-safe increment for joined matches. */
    public void recordJoin() {
        joinedMatchCount.incrementAndGet();
    }

    /** Thread-safe increment for completed matches. */
    public void recordCompletion() {
        completedMatchCount.incrementAndGet();
    }

    public int getJoinedMatchCount() {
        return joinedMatchCount.get();
    }

    public int getCompletedMatchCount() {
        return completedMatchCount.get();
    }

    /**
     * Returns a readable server stats line.
     * Format: Server stats: 3 joined, 2 completed
     */
    public String buildStatusLine() {
        return "Server stats: "
                + joinedMatchCount.get()
                + " joined, "
                + completedMatchCount.get()
                + " completed";
    }
}